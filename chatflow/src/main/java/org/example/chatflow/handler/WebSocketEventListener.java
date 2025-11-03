package org.example.chatflow.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.service.OnlineUserService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Map;

/**
 * WebSocket事件监听器
 * 监听WebSocket连接、断开、订阅等事件，管理在线用户状态
 * @author by zzr
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    
    private final OnlineUserService onlineUserService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * 监听WebSocket连接建立事件
     * 注意：SessionConnectedEvent是STOMP协议的ACK事件，发生在CONNECT命令之后
     * 此时用户信息已经由WebSocketChannelInterceptor在CONNECT阶段设置到Principal中
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        // 从Principal获取用户信息（由ChannelInterceptor在STOMP CONNECT阶段设置）
        java.security.Principal principal = headerAccessor.getUser();
        if (principal == null) {
            log.warn("WebSocket连接建立，但Principal为空: sessionId={}", sessionId);
            return;
        }
        
        Long userId;
        try {
            userId = Long.parseLong(principal.getName());
        } catch (NumberFormatException e) {
            log.warn("无法解析Principal中的用户ID: principal={}, sessionId={}", principal.getName(), sessionId);
            return;
        }
        
        // 从数据库获取完整用户信息
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.warn("WebSocket连接建立，但用户不存在: userId={}, sessionId={}", userId, sessionId);
            return;
        }
        
        // 记录用户上线
        try {
            onlineUserService.userOnline(sessionId, user);
            log.info("用户上线成功: userId={}, nickname={}, sessionId={}", userId, user.getNickname(), sessionId);
            
            // 广播用户上线消息
            broadcastOnlineUserCount();
        } catch (IllegalStateException e) {
            // 连接数超限（理论上不会到这里，因为ChannelInterceptor已经拦截了）
            log.error("用户上线失败（连接数超限）: userId={}, sessionId={}, error={}", 
                    userId, sessionId, e.getMessage());
        } catch (Exception e) {
            log.error("用户上线失败: userId={}, sessionId={}", userId, sessionId, e);
        }
    }
    
    /**
     * 监听WebSocket断开连接事件
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        // 获取断开连接的用户ID（用于日志）
        Long userId = onlineUserService.getUserIdBySessionId(sessionId);
        
        // 记录用户下线
        onlineUserService.userOffline(sessionId);
        
        log.info("WebSocket断开连接: userId={}, sessionId={}", userId, sessionId);
        
        // 广播在线用户数变化（可选）
        broadcastOnlineUserCount();
    }
    
    /**
     * 监听订阅事件（可选，用于更新用户活跃时间）
     */
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        
        log.debug("WebSocket订阅事件: sessionId={}, destination={}", sessionId, destination);
        
        // 更新用户活跃时间
        onlineUserService.updateLastActiveTime(sessionId);
    }
    
    /**
     * 广播在线用户数量变化
     */
    private void broadcastOnlineUserCount() {
        try {
            int count = onlineUserService.getOnlineUserCount();
            Map<String, Object> payload = Map.of(
                "type", "ONLINE_COUNT_UPDATE",
                "count", count,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend("/topic/online-count", payload);
            log.debug("广播在线用户数: {}", count);
        } catch (Exception e) {
            log.error("广播在线用户数失败", e);
        }
    }
}
