package org.example.chatflow.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.model.dto.MessagePushDTO;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.service.OnlineUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Redis消息订阅器
 * 监听Redis频道消息，推送给当前实例的在线用户
 * 实现多实例间的消息广播
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedisMessageSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final OnlineUserService onlineUserService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 1. 解析消息体
            String json = objectMapper.readValue(message.getBody(), String.class);
            MessagePushDTO pushDTO = objectMapper.readValue(json, MessagePushDTO.class);

            log.info("收到Redis广播消息: conversationId={}, senderId={}",
                    pushDTO.getConversationId(), pushDTO.getSenderId());

            // 2. 获取会话的所有参与者（从DTO中获取接收者列表）
            List<Long> receiverIds = pushDTO.getReceiverIds();
            if (receiverIds == null || receiverIds.isEmpty()) {
                log.warn("消息没有接收者列表，跳过推送");
                return;
            }

            // 3. 遍历接收者，检查是否在本实例在线且通知开启，如果是则推送
            for (Long receiverId : receiverIds) {
                if (onlineUserService.isUserOnline(receiverId)) {
                    // 检查用户通知设置
                    User user = userRepository.findById(receiverId).orElse(null);
                    if (user != null && user.getNotificationEnabled() != null && user.getNotificationEnabled() == 1) {
                        // 通知关闭，跳过推送
                        log.debug("用户通知已关闭，跳过推送: userId={}", receiverId);
                        continue;
                    }
                    String destination = "/user/" + receiverId + "/queue/pm";
                    messagingTemplate.convertAndSend(destination, pushDTO);
                    log.info("消息已推送给本地用户: userId={}, destination={}", receiverId, destination);
                }
            }

        } catch (IOException e) {
            log.error("解析Redis消息失败: {}", new String(message.getBody()), e);
        } catch (Exception e) {
            log.error("处理Redis消息时发生错误", e);
        }
    }
}
