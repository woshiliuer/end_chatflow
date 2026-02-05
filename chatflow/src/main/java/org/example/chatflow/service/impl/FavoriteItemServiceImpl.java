package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.constants.FileSourceTypeConstant;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.common.enums.ConversationType;
import org.example.chatflow.model.dto.favorite.FavoriteItemCollectFromMessageDTO;
import org.example.chatflow.model.dto.common.FileCommonDTO;
import org.example.chatflow.model.entity.ChatGroup;
import org.example.chatflow.model.entity.FileEntity;
import org.example.chatflow.model.entity.FavoriteItem;
import org.example.chatflow.model.entity.Conversation;
import org.example.chatflow.model.entity.Message;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.model.vo.favorite.FavoriteItemDetailVO;
import org.example.chatflow.model.vo.favorite.FavoriteItemPageVO;
import org.example.chatflow.common.constants.OssConstant;
import org.example.chatflow.model.vo.common.FileCommonVO;
import org.example.chatflow.repository.ChatGroupRepository;
import org.example.chatflow.repository.ConversationRepository;
import org.example.chatflow.repository.FileRepository;
import org.example.chatflow.repository.FavoriteItemRepository;
import org.example.chatflow.repository.MessageRepository;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.service.FavoriteItemService;
import org.example.chatflow.service.FileService;
import org.example.chatflow.support.CurrentUserAccessor;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FavoriteItemServiceImpl implements FavoriteItemService {

    private final FavoriteItemRepository favoriteItemRepository;
    private final CurrentUserAccessor currentUserAccessor;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<Long> collectFromMessage(FavoriteItemCollectFromMessageDTO dto) {
        if (dto == null || dto.getConversationId() == null || dto.getMessageId() == null) {
            VerifyUtil.isTrue(true, ErrorCode.VALIDATION_ERROR);
        }

        Long currentUserId = currentUserAccessor.getCurrentUser().getId();
        VerifyUtil.isTrue(currentUserId == null, ErrorCode.USER_NOT_LOGIN);

        Conversation conversation = conversationRepository.findById(dto.getConversationId()).orElse(null);
        if (conversation == null) {
            System.err.println("[Favorite] Conversation not found: " + dto.getConversationId());
            VerifyUtil.isTrue(true, ErrorCode.BUSINESS_ERROR);
        }

        Message message = messageRepository.findById(dto.getMessageId()).orElse(null);
        if (message == null) {
            System.err.println("[Favorite] Message not found: " + dto.getMessageId());
            VerifyUtil.isTrue(true, ErrorCode.BUSINESS_ERROR);
        }
        
        if (!dto.getConversationId().equals(message.getConversationId())) {
            System.err.println("[Favorite] Conversation mismatch: dto=" + dto.getConversationId() + ", msg=" + message.getConversationId());
            VerifyUtil.isTrue(true, ErrorCode.BUSINESS_ERROR);
        }

        User sender = userRepository.findById(message.getSenderId()).orElse(null);
        if (sender == null) {
            System.err.println("[Favorite] Sender not found: " + message.getSenderId());
            VerifyUtil.isTrue(true, ErrorCode.BUSINESS_ERROR);
        }

        FavoriteItem favoriteItem = new FavoriteItem();
        favoriteItem.setUserId(currentUserId);
        favoriteItem.setItemType(message.getMessageType());
        favoriteItem.setSenderId(message.getSenderId());
        favoriteItem.setSendTime(message.getSendTime());

        Integer conversationType = conversation.getConversationType();
        if (ConversationType.GROUP.getCode().equals(conversationType)) {
            favoriteItem.setSourceType(2);
            Long groupId = conversation.getGroupId();
            VerifyUtil.isTrue(groupId == null, ErrorCode.BUSINESS_ERROR);
            ChatGroup group = chatGroupRepository.findNormalById(groupId);
            VerifyUtil.isTrue(group == null, ErrorCode.BUSINESS_ERROR);
            favoriteItem.setGroupId(group.getId());
        } else if (ConversationType.PRIVATE.getCode().equals(conversationType)) {
            favoriteItem.setSourceType(1);
        } else {
            favoriteItem.setSourceType(3);
        }

        Integer messageType = message.getMessageType();
        if (Integer.valueOf(1).equals(messageType)) {
            favoriteItem.setTextContent(message.getContent());
        } else if (Integer.valueOf(2).equals(messageType)) {
            // 表情/图片：复制消息文件，绑定到新的收藏项
            FileEntity origin = fileRepository.findLatestBySource(FileSourceTypeConstant.MESSAGE_FILE, message.getId());
            VerifyUtil.isTrue(origin == null, ErrorCode.BUSINESS_ERROR);

            VerifyUtil.ensureOperationSucceeded(favoriteItemRepository.save(favoriteItem), ErrorCode.INTERNAL_ERROR);
            VerifyUtil.isTrue(favoriteItem.getId() == null, ErrorCode.INTERNAL_ERROR);

            FileCommonDTO file = new FileCommonDTO();
            file.setSourceType(FileSourceTypeConstant.FAVORITE_ITEM_FILE);
            file.setSourceId(favoriteItem.getId());
            file.setFileType(origin.getFileType());
            file.setFileName(origin.getFileName());
            file.setFileSize(origin.getFileSize());
            file.setFilePath(origin.getFilePath());
            file.setFileDesc(origin.getFileDesc());
            VerifyUtil.ensureOperationSucceeded(fileService.saveFile(file), ErrorCode.INTERNAL_ERROR);
            return CurlResponse.success(favoriteItem.getId());
        }

        VerifyUtil.ensureOperationSucceeded(favoriteItemRepository.save(favoriteItem), ErrorCode.INTERNAL_ERROR);
        return CurlResponse.success(favoriteItem.getId());
    }

    @Override
    public CurlResponse<List<FavoriteItemPageVO>> list() {
        Long currentUserId = currentUserAccessor.getCurrentUser().getId();
        VerifyUtil.isTrue(currentUserId == null, ErrorCode.USER_NOT_LOGIN);

        List<FavoriteItem> list = favoriteItemRepository.listByUserId(currentUserId, null);
        List<FavoriteItemPageVO> voList = list.stream().map(item -> {
                FavoriteItemPageVO vo = new FavoriteItemPageVO();
                vo.setId(item.getId());
                if (Integer.valueOf(1).equals(item.getItemType())) {
                    vo.setContent(item.getTextContent());
                } else {
                    vo.setContent("[动画表情]");
                }
                vo.setSourceType(item.getSourceType());
                
                // 从用户表获取发送人姓名
                User sender = userRepository.findById(item.getSenderId()).orElse(null);
                vo.setSenderName(sender != null ? sender.getNickname() : "未知用户");
                vo.setSendTime(item.getSendTime());
                
                // 群聊时从群聊表获取群名
                if (Integer.valueOf(2).equals(item.getSourceType()) && item.getGroupId() != null) {
                    ChatGroup group = chatGroupRepository.findNormalById(item.getGroupId());
                    vo.setGroupName(group != null ? group.getGroupName() : "未知群聊");
                }
                return vo;
            }).collect(Collectors.toList());
        return CurlResponse.success(voList);
    }

    @Override
    public CurlResponse<FavoriteItemDetailVO> detail(Long favoriteId) {
        VerifyUtil.isTrue(favoriteId == null, ErrorCode.VALIDATION_ERROR);

        Long currentUserId = currentUserAccessor.getCurrentUser().getId();
        VerifyUtil.isTrue(currentUserId == null, ErrorCode.USER_NOT_LOGIN);

        FavoriteItem item = favoriteItemRepository.findById(favoriteId).orElse(null);
        VerifyUtil.isTrue(item == null, ErrorCode.BUSINESS_ERROR);
        VerifyUtil.isTrue(!currentUserId.equals(item.getUserId()), ErrorCode.BUSINESS_ERROR);

        FavoriteItemDetailVO vo = new FavoriteItemDetailVO();
        vo.setId(item.getId());
        vo.setItemType(item.getItemType());
        vo.setSourceType(item.getSourceType());
        vo.setSenderId(item.getSenderId());
        vo.setSendTime(item.getSendTime());

        // 获取发送人信息和头像
        User sender = userRepository.findById(item.getSenderId()).orElse(null);
        if (sender != null) {
            vo.setSenderName(sender.getNickname());
            // 获取用户头像
            FileEntity userAvatar = fileRepository.findLatestBySource(FileSourceTypeConstant.USER_AVATAR, sender.getId());
            if (userAvatar != null) {
                vo.setSenderAvatar(FileCommonVO.FileCommonVOMapper.INSTANCE.toVO(userAvatar, OssConstant.buildFullUrl(userAvatar.getFilePath())));
            }
        } else {
            vo.setSenderName("未知用户");
        }

        // 群聊信息
        if (Integer.valueOf(2).equals(item.getSourceType()) && item.getGroupId() != null) {
            vo.setGroupId(item.getGroupId());
            ChatGroup group = chatGroupRepository.findNormalById(item.getGroupId());
            if (group != null) {
                vo.setGroupName(group.getGroupName());
                // 获取群头像
                FileEntity groupAvatar = fileRepository.findLatestBySource(FileSourceTypeConstant.GROUP_AVATAR, group.getId());
                if (groupAvatar != null) {
                    vo.setGroupAvatar(FileCommonVO.FileCommonVOMapper.INSTANCE.toVO(groupAvatar, OssConstant.buildFullUrl(groupAvatar.getFilePath())));
                }
            } else {
                vo.setGroupName("未知群聊");
            }
        }

        // 内容处理
        if (Integer.valueOf(1).equals(item.getItemType())) {
            // 文本内容
            vo.setContent(item.getTextContent());
        } else {
            // 图片/表情内容，获取文件信息
            FileEntity file = fileRepository.findLatestBySource(FileSourceTypeConstant.FAVORITE_ITEM_FILE, item.getId());
            if (file != null) {
                vo.setContent(file.getFileName());
                vo.setFileDetail(FileCommonVO.FileCommonVOMapper.INSTANCE.toVO(file, OssConstant.buildFullUrl(file.getFilePath())));
            } else {
                vo.setContent("[文件不存在]");
            }
        }

        return CurlResponse.success(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<Boolean> delete(Long favoriteId) {
        VerifyUtil.isTrue(favoriteId == null, ErrorCode.VALIDATION_ERROR);

        Long currentUserId = currentUserAccessor.getCurrentUser().getId();
        VerifyUtil.isTrue(currentUserId == null, ErrorCode.USER_NOT_LOGIN);

        FavoriteItem item = favoriteItemRepository.findById(favoriteId).orElse(null);
        VerifyUtil.isTrue(item == null, ErrorCode.BUSINESS_ERROR);
        VerifyUtil.isTrue(!currentUserId.equals(item.getUserId()), ErrorCode.BUSINESS_ERROR);

        // 1. 删除收藏项绑定的文件记录
        fileRepository.deleteBySource(FileSourceTypeConstant.FAVORITE_ITEM_FILE, favoriteId);

        // 2. 删除收藏项
        VerifyUtil.ensureOperationSucceeded(favoriteItemRepository.deleteById(favoriteId), ErrorCode.INTERNAL_ERROR);

        return CurlResponse.success(true);
    }
}
