package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.common.constants.FileSourceTypeConstant;
import org.example.chatflow.common.constants.OssConstant;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.Direction;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.model.dto.MessagePushDTO;
import org.example.chatflow.model.dto.SendMessageDTO;
import org.example.chatflow.model.dto.common.FileCommonDTO;
import org.example.chatflow.model.entity.Conversation;
import org.example.chatflow.model.entity.ConversationUser;
import org.example.chatflow.model.entity.Message;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.model.vo.MessageVO;
import org.example.chatflow.model.vo.common.FileCommonVO;
import org.example.chatflow.repository.ConversationRepository;
import org.example.chatflow.repository.ConversationUserRepository;
import org.example.chatflow.repository.MessageRepository;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.service.FileService;
import org.example.chatflow.service.MessageService;
import org.example.chatflow.service.OnlineUserService;
import org.example.chatflow.support.CurrentUserAccessor;
import org.example.chatflow.utils.AliOssUtil;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by zzr
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationUserRepository conversationUserRepository;
    private final CurrentUserAccessor currentUserAccessor;
    private final OnlineUserService onlineUserService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final FileService fileService;
    /**
     * 消息列表
     */
    @Override
    public CurlResponse<List<MessageVO>> messageList(Long param) {
        User currentUser = currentUserAccessor.getCurrentUser();
        List<Message>  messageList = messageRepository.findByConversationIds(Collections.singleton(param));
        //消息列表提取发送Id到Set
        Set<Long> userIds = messageList.stream().map(
                Message::getSenderId
        ).collect(Collectors.toSet());

        //批量查询用户信息出来,分组成map
        Map<Long,User> userMap = userRepository.findUserMapByIds(userIds);

        Map<Long, String> avatarByUserId = fileService.getLatestFullUrlMap(
                FileSourceTypeConstant.USER_AVATAR,
                userIds,
                OssConstant.DEFAULT_AVATAR
        );

        Set<Long> messageIds = messageList.stream()
                .map(Message::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, FileCommonVO> messageFileMap = fileService.getBySourceMap(
                FileSourceTypeConstant.MESSAGE_FILE,
                messageIds
        );

        List<MessageVO>  messageVOList = new ArrayList<>();
        for (Message message:messageList){
            MessageVO messageVO = MessageVO.MessageVOMapper.INSTANCE.toVO(message);
            Direction direction;
            User user = userMap.get(message.getSenderId());
            VerifyUtil.isTrue(user == null , ErrorCode.SENDER_NOT_EXISTS);
            if (message.getSenderId().equals(currentUser.getId())){
                direction = Direction.USER_TO_FRIEND;
            }else{
                direction = Direction.FRIEND_TO_USER;
            }
            messageVO.setAvatarFullUrl(avatarByUserId.get(user.getId()));
            messageVO.setDirection(direction.getCode());
            messageVO.setMessageFile(messageFileMap.get(message.getId()));
            messageVOList.add(messageVO);
        }
        return CurlResponse.success(messageVOList);
    }
    
    /**
     * 发送消息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<MessageVO> sendMessage(SendMessageDTO dto) {
        User currentUser = currentUserAccessor.getCurrentUser();
        Long senderId = currentUser.getId();

        //查询接收者用户Id
        User sender = userRepository.findById(senderId).orElse(null);
        VerifyUtil.isTrue(sender == null , ErrorCode.SENDER_NOT_EXISTS);

        // 1. 验证会话是否存在
        Conversation conversation = conversationRepository.findById(dto.getConversationId())
                .orElseThrow(() -> new RuntimeException(ErrorCode.CONVERSATION_NOT_FOUND.getMessage()));
        
        // 2. 查询会话的所有参与者
        List<ConversationUser> conversationUsers = conversationUserRepository
                .findByConversationIds(Collections.singleton(dto.getConversationId()));
        VerifyUtil.isTrue(conversationUsers == null || conversationUsers.isEmpty(), 
                ErrorCode.CONVERSATION_USER_NOT_FOUND);
        
        // 3. 获取所有接收者ID（排除发送者自己）
        List<Long> receiverIds = conversationUsers.stream()
                .map(ConversationUser::getMemberId)
                .filter(memberId -> !memberId.equals(senderId))
                .collect(Collectors.toList());
        
        // 4. 获取下一个序号
        Long nextSequence = messageRepository.getMaxSequenceByConversationId(dto.getConversationId()) + 1;
        
        // 5. 构建消息实体
        Message message = new Message();
        message.setConversationId(dto.getConversationId());
        message.setSenderId(senderId);
        message.setMessageType(dto.getMessageType());
        message.setSequence(nextSequence);
        message.setStatus(1); // 1正常
        message.setSendTime(System.currentTimeMillis());
        if (dto.getMessageType() == 1){
            message.setContent(dto.getContent());
        }

        // 6. 保存消息到数据库
        messageRepository.save(message);

        // 6.1 保存文件关联，并构建回显/推送用的 messageFile
        FileCommonVO messageFile = null;
        if (dto.getMessageType() == 2) {
            FileCommonDTO file = dto.getMessageFile();
            VerifyUtil.isTrue(file == null, ErrorCode.VALIDATION_ERROR);

            file.setSourceType(FileSourceTypeConstant.MESSAGE_FILE);
            file.setSourceId(message.getId());

            fileService.saveFile(file);

            messageFile = FileCommonVO.FileCommonVOMapper.INSTANCE.toVO(
                    FileCommonDTO.FileCommonDTOMapper.INSTANCE.toEntity(file),
                    OssConstant.buildFullUrl(file.getFilePath())
            );
        }
        
        // 7. 更新会话的最新消息信息
        conversation.setLastMessageId(message.getId());
        conversation.setLastMessageTime(message.getSendTime());
        conversationRepository.update(conversation);



        // 8. 构建WebSocket推送消息
        MessagePushDTO pushDTO = MessagePushDTO.builder()
                .id(message.getId())
                .messageId(message.getId()) // 兼容旧字段
                .conversationId(message.getConversationId())
                .from(senderId)
                .senderId(senderId) // 兼容旧字段
                .senderNickname(currentUser.getNickname())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .messageFile(messageFile)
                .sequence(message.getSequence())
                .sendTime(message.getSendTime())
                .status(message.getStatus())
                .avatarFullUrl(fileService.getLatestFullUrl(
                        FileSourceTypeConstant.USER_AVATAR,
                        sender.getId(),
                        OssConstant.DEFAULT_AVATAR
                ))
                .build();
        
        // 9. 通过WebSocket推送消息给所有接收者（如果在线）
        for (Long receiverId : receiverIds) {
            if (onlineUserService.isUserOnline(receiverId)) {
                String destination = "/user/" + receiverId + "/queue/pm";
                messagingTemplate.convertAndSend(destination, pushDTO);
                //TODO 如果发送更成功还要设置为已读
                log.info("消息已推送给接收者 userId={}, destination={}", receiverId, destination);
            } else {
                log.info("接收者不在线，消息已保存 userId={}", receiverId);
            }
        }
        
        // 注意：不再通过WebSocket推送给发送者本人，因为：
        // 1. 前端已通过HTTP响应获得完整消息信息并做了乐观更新
        // 2. 避免发送者看到重复的消息
        // 3. 如果未来需要支持多端同步，可以在此处添加逻辑，但需要前端配合去重
        
        // 11. 构建返回VO
        MessageVO messageVO = MessageVO.MessageVOMapper.INSTANCE.toVO(message);
        messageVO.setDirection(Direction.USER_TO_FRIEND.getCode());
        messageVO.setMessageFile(messageFile);
        
        return CurlResponse.success(messageVO);
    }
    
    /**
     * 标记会话消息为已读
     * 将当前用户在指定会话的所有消息标记为已读
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<Void> markAsRead(Long conversationId) {
        User currentUser = currentUserAccessor.getCurrentUser();
        Long userId = currentUser.getId();
        
        // 1. 验证会话是否存在
        conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException(ErrorCode.CONVERSATION_NOT_FOUND.getMessage()));
        
        // 2. 查询当前用户在该会话的记录
        ConversationUser conversationUser = conversationUserRepository
                .findByConversationIdAndMemberId(conversationId, userId);
        VerifyUtil.isTrue(conversationUser == null, ErrorCode.CONVERSATION_USER_NOT_FOUND);
        
        // 3. 获取该会话的最新消息序号
        Long maxSequence = messageRepository.getMaxSequenceByConversationId(conversationId);
        
        // 4. 只有当新的sequence大于当前last_read_seq时才更新
        if (maxSequence != null && maxSequence > 0) {
            if (conversationUser.getLastReadSeq() == null || maxSequence > conversationUser.getLastReadSeq()) {
                conversationUser.setLastReadSeq(maxSequence);
                conversationUser.setLastReadTime(System.currentTimeMillis());
                conversationUserRepository.update(conversationUser);
                log.info("标记已读成功 userId={}, conversationId={}, lastReadSeq={}", 
                        userId, conversationId, maxSequence);
            } else {
                log.debug("无需更新已读状态 userId={}, conversationId={}, currentLastReadSeq={}, maxSequence={}", 
                        userId, conversationId, conversationUser.getLastReadSeq(), maxSequence);
            }
        }
        
        return CurlResponse.success();
    }
}
