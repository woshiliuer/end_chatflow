package org.example.chatflow.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.common.constants.FileSourceTypeConstant;
import org.example.chatflow.common.constants.OssConstant;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.*;
import org.example.chatflow.model.entity.ChatGroup;
import org.example.chatflow.model.entity.Conversation;
import org.example.chatflow.model.entity.ConversationUser;
import org.example.chatflow.model.entity.FriendRelation;
import org.example.chatflow.model.entity.Message;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.model.vo.SessionVO;
import org.example.chatflow.repository.ChatGroupRepository;
import org.example.chatflow.repository.ConversationRepository;
import org.example.chatflow.repository.ConversationUserRepository;
import org.example.chatflow.repository.FriendRelationRepository;
import org.example.chatflow.repository.MessageRepository;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.service.ConversationService;
import org.example.chatflow.service.FileService;
import org.example.chatflow.support.CurrentUserAccessor;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author by zzr
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationUserRepository conversationUserRepository;
    private final UserRepository userRepository;
    private final FriendRelationRepository friendRelationRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final MessageRepository messageRepository;
    private final CurrentUserAccessor currentUserAccessor;
    private final FileService fileService;

    /**
     * 会话列表（仅查询数据库会话基础信息）
     */
    @Override
    public CurlResponse<List<SessionVO>> getSessionList() {
        User user = currentUserAccessor.getCurrentUser();
        List<ConversationUser> relations = conversationUserRepository.findByMemberId(user.getId());
        if (relations.isEmpty()) {
            return CurlResponse.success(Collections.emptyList());
        }

        Map<Long, Long> lastReadSeqByConversation = new LinkedHashMap<>();
        Map<Long, Long> visibleSeqByConversation = new LinkedHashMap<>();
        Map<Long, Integer> statusByConversation = new LinkedHashMap<>();
        for (ConversationUser relation : relations) {
            Long conversationId = relation.getConversationId();
            if (conversationId == null) {
                continue;
            }
            int status = relation.getStatus() == null
                ? ConversationStatus.NORMAL.getCode()
                : relation.getStatus();
            if (ConversationStatus.HIDDEN.getCode() == status) {
                continue;
            }
            statusByConversation.putIfAbsent(conversationId, status);
            long lastReadSeq = relation.getLastReadSeq() == null ? 0L : relation.getLastReadSeq();
            lastReadSeqByConversation.putIfAbsent(conversationId, lastReadSeq);
            
            // 收集每个用户在每个会话的可见起点
            long visibleSeq = relation.getVisibleSeq() == null ? 0L : relation.getVisibleSeq();
            visibleSeqByConversation.putIfAbsent(conversationId, visibleSeq);
        }
        if (statusByConversation.isEmpty()) {
            return CurlResponse.success(Collections.emptyList());
        }

        Set<Long> conversationIds = new LinkedHashSet<>(statusByConversation.keySet());
        List<Conversation> conversations = conversationRepository.findByIds(conversationIds);
        if (conversations.isEmpty()) {
            return CurlResponse.success(Collections.emptyList());
        }

        // 先分离私聊/群聊，后续批量查各自需要的补充信息
        ConversationBuckets buckets = categorizeConversations(conversations);

        Map<Long, User> privateConversationUserMap = loadPrivateConversationPartners(
            user.getId(), buckets.privateConversations());

        Map<Long, ChatGroup> groupConversationMap = loadGroupConversationInfo(buckets.groupConversations());

        Set<Long> partnerIds = privateConversationUserMap.values().stream()
                .filter(Objects::nonNull)
                .map(User::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<Long, String> userAvatarByUserId = fileService.getLatestFullUrlMap(
                FileSourceTypeConstant.USER_AVATAR,
                partnerIds,
                OssConstant.DEFAULT_AVATAR
        );

        Set<Long> groupIds = groupConversationMap.values().stream()
                .filter(Objects::nonNull)
                .map(ChatGroup::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<Long, String> groupAvatarByGroupId = fileService.getLatestFullUrlMap(
                FileSourceTypeConstant.GROUP_AVATAR,
                groupIds,
                OssConstant.DEFAULT_GROUP_AVATAR
        );

        // 拉取消息明细，用于补全最新消息和未读统计（需要根据当前用户的可见起点过滤）
        Map<Long, List<Message>> messagesByConversation = loadMessagesByConversationIdsWithVisibleSeq(
                conversationIds, visibleSeqByConversation, user.getId());
        Map<Long, Message> lastMessageMap = buildLastMessageMap(conversations, messagesByConversation);

        Map<Long, Integer> unreadCountMap = calculateUnreadCounts(
            messagesByConversation, lastReadSeqByConversation, user.getId());

        List<SessionVO> sessionVOList = buildSessionList(conversations,
            privateConversationUserMap,
            groupConversationMap,
            lastMessageMap,
            unreadCountMap,
            statusByConversation,
            userAvatarByUserId,
            groupAvatarByGroupId,
            user.getId());

        return CurlResponse.success(sessionVOList);
    }


    /**
     * 设置为常用会话
     */
    @Override
    public CurlResponse<String> setFavorite(Long param) {
        User user = currentUserAccessor.getCurrentUser();
        ConversationUser conversationUser = conversationUserRepository.findByConversationIdAndMemberId(param, user.getId());
        VerifyUtil.isTrue(conversationUser == null, ErrorCode.CONVERSATION_RELATION_NOT_EXISTS);
        if (Objects.equals(conversationUser.getStatus(), ConversationStatus.NORMAL.getCode())) {
            conversationUser.setStatus(ConversationStatus.FAVORITE.getCode());
            VerifyUtil.ensureOperationSucceeded(conversationUserRepository.update(conversationUser),
                    ErrorCode.CONVERSATION_USER_UPDATE_FAIL);
        }
        return CurlResponse.success("成功设置为常用会话");
    }

    /**
     * 取消常用会话
     */
    @Override
    public CurlResponse<String> cancelFavorite(Long param) {
        User user = currentUserAccessor.getCurrentUser();
        ConversationUser conversationUser = conversationUserRepository.findByConversationIdAndMemberId(param, user.getId());
        VerifyUtil.isTrue(conversationUser == null, ErrorCode.CONVERSATION_RELATION_NOT_EXISTS);
        if (!Objects.equals(conversationUser.getStatus(), ConversationStatus.NORMAL.getCode())) {
            conversationUser.setStatus(ConversationStatus.NORMAL.getCode());
            VerifyUtil.ensureOperationSucceeded(conversationUserRepository.update(conversationUser),
                    ErrorCode.CONVERSATION_USER_UPDATE_FAIL);
        }
        return CurlResponse.success("成功取消常用会话");
    }

    /**
     * 删除会话（标记为隐藏，并推进可见起点）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<String> deleteConversation(Long param) {
        User user = currentUserAccessor.getCurrentUser();
        ConversationUser conversationUser = conversationUserRepository.findByConversationIdAndMemberId(param, user.getId());
        VerifyUtil.isTrue(conversationUser == null, ErrorCode.CONVERSATION_RELATION_NOT_EXISTS);

        // 1. 获取会话当前最新的消息序号
        Long maxSequence = messageRepository.getMaxSequenceByConversationId(param);

        // 2. 更新可见起点（即便已经隐藏，再次删除也会更新起点，达到清空效果）
        conversationUser.setVisibleSeq(maxSequence != null ? maxSequence : 0L);

        // 3. 设置状态为隐藏
        conversationUser.setStatus(ConversationStatus.HIDDEN.getCode());

        VerifyUtil.ensureOperationSucceeded(conversationUserRepository.update(conversationUser),
                ErrorCode.CONVERSATION_USER_UPDATE_FAIL);

        return CurlResponse.success("删除成功");
    }

    /**
     * 新增或恢复单聊会话（基于好友关系）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long ensurePrivateConversation(Long userId, Long friendId) {

        VerifyUtil.isTrue(Objects.equals(userId, friendId), ErrorCode.CONVERSATION_SELF_NOT_ALLOWED);

        User requester = userRepository.findById(userId).orElse(null);
        VerifyUtil.isTrue(requester == null, ErrorCode.USER_NOT_EXISTS);
        User friend = userRepository.findById(friendId).orElse(null);
        VerifyUtil.isTrue(friend == null, ErrorCode.USER_NOT_EXISTS);

        verifyFriendRelation(userId, friendId);

        Long existedConversationId = findExistingPrivateConversation(userId, friendId);
        if (existedConversationId != null) {
            restoreConversationStatus(existedConversationId, userId);
            restoreConversationStatus(existedConversationId, friendId);
            return existedConversationId;
        }

        Conversation conversation = new Conversation();
        conversation.setConversationType(ConversationType.PRIVATE.getCode());
        VerifyUtil.ensureOperationSucceeded(conversationRepository.save(conversation),
                ErrorCode.CONVERSATION_SAVE_FAIL);

        ConversationUser requesterRelation = buildConversationUser(conversation.getId(), userId);
        ConversationUser friendRelation = buildConversationUser(conversation.getId(), friendId);

        VerifyUtil.ensureOperationSucceeded(
                conversationUserRepository.saveBatch(List.of(requesterRelation, friendRelation)),
                ErrorCode.CONVERSATION_USER_SAVE_FAIL
        );

        return conversation.getId();
    }

    private void verifyFriendRelation(Long userId, Long friendId) {
        FriendRelation relation = friendRelationRepository.findByUserAndFriendId(userId, friendId);
        FriendRelation reverseRelation = friendRelationRepository.findByUserAndFriendId(friendId, userId);
        boolean validRelation = relation != null && !Objects.equals(relation.getDeleted(), Deleted.HAS_DELETED.getCode())
            && reverseRelation != null && !Objects.equals(reverseRelation.getDeleted(), Deleted.HAS_DELETED.getCode());
        VerifyUtil.isFalse(validRelation, ErrorCode.FRIEND_RELATION_NOT_EXISTS);
    }

    @Override
    public Long findExistingPrivateConversation(Long userId, Long friendId) {
        List<ConversationUser> relations = conversationUserRepository.findAllByMemberId(userId);
        if (relations == null || relations.isEmpty()) {
            return null;
        }
        Set<Long> conversationIds = relations.stream()
                .map(ConversationUser::getConversationId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (conversationIds.isEmpty()) {
            return null;
        }
        List<Conversation> conversations = conversationRepository.findByIds(conversationIds);
        Set<Long> privateConversationIds = conversations.stream()
                .filter(conversation -> Objects.equals(conversation.getConversationType(), ConversationType.PRIVATE.getCode()))
                .map(Conversation::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (privateConversationIds.isEmpty()) {
            return null;
        }
        for (Long conversationId : privateConversationIds) {
            ConversationUser friendRelation = conversationUserRepository.findByConversationIdAndMemberId(conversationId, friendId);
            if (friendRelation != null) {
                return conversationId;
            }
        }
        return null;
    }

    @Override
    public CurlResponse<Long> restoreByGroup(Long groupId) {
        User user = currentUserAccessor.getCurrentUser();
        Conversation conversation = conversationRepository.findByGroupId(groupId);
        VerifyUtil.isTrue(conversation == null, ErrorCode.CONVERSATION_NOT_FOUND);
        ConversationUser relation = conversationUserRepository.findByConversationIdAndMemberId(conversation.getId(), user.getId());
        VerifyUtil.isTrue(relation == null, ErrorCode.CONVERSATION_RELATION_NOT_EXISTS);
        if (Objects.equals(relation.getStatus(), ConversationStatus.HIDDEN.getCode())) {
            relation.setStatus(ConversationStatus.NORMAL.getCode());
            VerifyUtil.ensureOperationSucceeded(conversationUserRepository.update(relation),
                    ErrorCode.CONVERSATION_USER_UPDATE_FAIL);
        }
        return CurlResponse.success(conversation.getId());
    }

    @Override
    public CurlResponse<Long> restoreByFriend(Long friendId) {
        User user = currentUserAccessor.getCurrentUser();
        Long conversationId = findExistingPrivateConversation(user.getId(), friendId);
        VerifyUtil.isTrue(conversationId == null, ErrorCode.CONVERSATION_NOT_FOUND);
        ConversationUser relation = conversationUserRepository.findByConversationIdAndMemberId(conversationId, user.getId());
        VerifyUtil.isTrue(relation == null, ErrorCode.CONVERSATION_RELATION_NOT_EXISTS);
        if (Objects.equals(relation.getStatus(), ConversationStatus.HIDDEN.getCode())) {
            relation.setStatus(ConversationStatus.NORMAL.getCode());
            VerifyUtil.ensureOperationSucceeded(conversationUserRepository.update(relation),
                    ErrorCode.CONVERSATION_USER_UPDATE_FAIL);
        }
        return CurlResponse.success(conversationId);
    }

    private ConversationUser buildConversationUser(Long conversationId, Long memberId) {
        ConversationUser conversationUser = new ConversationUser();
        conversationUser.setConversationId(conversationId);
        conversationUser.setMemberId(memberId);
        conversationUser.setStatus(ConversationStatus.NORMAL.getCode());
        return conversationUser;
    }

    private void restoreConversationStatus(Long conversationId, Long memberId) {
        ConversationUser relation = conversationUserRepository.findByConversationIdAndMemberId(conversationId, memberId);
        if (relation != null && Objects.equals(relation.getStatus(), ConversationStatus.HIDDEN.getCode())) {
            relation.setStatus(ConversationStatus.NORMAL.getCode());
            VerifyUtil.ensureOperationSucceeded(conversationUserRepository.update(relation),
                ErrorCode.CONVERSATION_USER_UPDATE_FAIL);
        }
    }

    private ConversationBuckets categorizeConversations(List<Conversation> conversations) {
        if (conversations == null || conversations.isEmpty()) {
            return new ConversationBuckets(Collections.emptyList(), Collections.emptyList());
        }
        List<Conversation> privateConversations = new ArrayList<>();
        List<Conversation> groupConversations = new ArrayList<>();
        for (Conversation conversation : conversations) {
            Integer type = conversation.getConversationType();
            if (ConversationType.PRIVATE.getCode().equals(type)) {
                privateConversations.add(conversation);
            } else if (ConversationType.GROUP.getCode().equals(type)) {
                groupConversations.add(conversation);
            }
        }
        return new ConversationBuckets(privateConversations, groupConversations);
    }

    private List<SessionVO> buildSessionList(List<Conversation> conversations,
                                             Map<Long, User> privateConversationUserMap,
                                             Map<Long, ChatGroup> groupConversationMap,
                                             Map<Long, Message> lastMessageMap,
                                             Map<Long, Integer> unreadCountMap,
                                             Map<Long, Integer> statusByConversation,
                                             Map<Long, String> userAvatarByUserId,
                                             Map<Long, String> groupAvatarByGroupId,
                                             Long currentUserId) {
        // 逐个会话组装输出字段，重用预先拉好的数据 
        List<SessionVO> sessionVOList = new ArrayList<>(conversations.size());
        for (Conversation conversation : conversations) {
            Message message = lastMessageMap.get(conversation.getId());
            int unreadCount = unreadCountMap.getOrDefault(conversation.getId(), 0);
            int status = statusByConversation.getOrDefault(conversation.getId(), ConversationStatus.NORMAL.getCode());
            SessionVO sessionVO = SessionVO.SessionVOMapper.INSTANCE.toVO(conversation, message, unreadCount, status);
            if (message != null && MessageType.EMOJI.getCode().equals(message.getMessageType())){
                sessionVO.setContent("[动画表情]");
            }
            if (ConversationType.PRIVATE.getCode().equals(conversation.getConversationType())) {
                User partner = privateConversationUserMap.get(conversation.getId());
                if (partner != null) {
                    sessionVO.setRelationId(partner.getId());
                    // 优先显示备注
                    FriendRelation relation = friendRelationRepository.findByUserAndFriendId(currentUserId, partner.getId());
                    String displayName = (relation != null && !com.aliyuncs.utils.StringUtils.isEmpty(relation.getRemark())) 
                            ? relation.getRemark() 
                            : partner.getNickname();
                    sessionVO.setDisplayName(displayName);
                    sessionVO.setAvatarFullUrl(userAvatarByUserId == null ? null : userAvatarByUserId.get(partner.getId()));
                }
            } else if (ConversationType.GROUP.getCode().equals(conversation.getConversationType())) {
                ChatGroup chatGroup = groupConversationMap.get(conversation.getId());
                if (chatGroup != null) {
                    sessionVO.setRelationId(chatGroup.getId());
                    sessionVO.setDisplayName(chatGroup.getGroupName());
                    sessionVO.setAvatarFullUrl(groupAvatarByGroupId == null ? null : groupAvatarByGroupId.get(chatGroup.getId()));
                }
            }
            sessionVOList.add(sessionVO);
        }
        return sessionVOList;
    }

    private Map<Long, List<Message>> loadMessagesByConversationIdsWithVisibleSeq(
            Set<Long> conversationIds, Map<Long, Long> visibleSeqByConversation, Long currentUserId) {
        if (conversationIds == null || conversationIds.isEmpty()) {
            return Collections.emptyMap();
        }
        
        Map<Long, List<Message>> result = new LinkedHashMap<>();
        
        // 为每个会话单独查询，应用该用户的可见起点过滤
        for (Long conversationId : conversationIds) {
            Long visibleSeq = visibleSeqByConversation.getOrDefault(conversationId, 0L);
            List<Message> messages = messageRepository.findByConversationIds(
                    Collections.singleton(conversationId), visibleSeq);
            result.put(conversationId, messages != null ? messages : new ArrayList<>());
        }
        
        return result;
    }

    private Map<Long, List<Message>> loadMessagesByConversationIds(Set<Long> conversationIds) {
        if (conversationIds == null || conversationIds.isEmpty()) {
            return Collections.emptyMap();
        }
        // 这里的批量查询用于会话列表，不针对特定用户过滤可见起点（或可以传入 null/0）
        List<Message> messages = messageRepository.findByConversationIds(conversationIds, 0L);
        Map<Long, List<Message>> grouped = (messages == null || messages.isEmpty())
            ? new LinkedHashMap<>()
            : messages.stream()
                .filter(message -> message.getConversationId() != null)
                .collect(Collectors.groupingBy(
                    Message::getConversationId,
                    LinkedHashMap::new,
                    Collectors.toCollection(ArrayList::new)
                ));
        conversationIds.forEach(id -> grouped.computeIfAbsent(id, key -> new ArrayList<>()));
        return grouped;
    }

    private Map<Long, Message> buildLastMessageMap(List<Conversation> conversations,
                                                   Map<Long, List<Message>> messagesByConversation) {
        Map<Long, Message> result = new LinkedHashMap<>();
        for (Conversation conversation : conversations) {
            Long conversationId = conversation.getId();
            if (conversationId == null) {
                continue;
            }
            
            // 直接从消息列表中获取最新消息（已按序号排序）
            List<Message> messageList = messagesByConversation.get(conversationId);
            if (messageList != null && !messageList.isEmpty()) {
                Message lastMessage = messageList.get(messageList.size() - 1);
                result.put(conversationId, lastMessage);
            }
        }
        return result;
    }

    private Map<Long, Integer> calculateUnreadCounts(Map<Long, List<Message>> messagesByConversation,
                                                     Map<Long, Long> lastReadSeqByConversation,
                                                     Long currentUserId) {
        if (messagesByConversation == null || messagesByConversation.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, Integer> unreadCounts = new LinkedHashMap<>();
        messagesByConversation.forEach((conversationId, messageList) -> {
            long lastReadSeq = lastReadSeqByConversation.getOrDefault(conversationId, 0L);
            int unread = 0;
            for (Message message : messageList) {
                Long sequence = message.getSequence();
                if (sequence == null) {
                    continue;
                }
                if (sequence > lastReadSeq && !Objects.equals(message.getSenderId(), currentUserId)) {
                    unread++;
                }
            }
            unreadCounts.put(conversationId, unread);
        });
        return unreadCounts;
    }

    private Map<Long, User> loadPrivateConversationPartners(Long currentUserId, List<Conversation> privateConversations) {
        if (privateConversations == null || privateConversations.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> privateConversationIds = extractConversationIds(privateConversations);
        if (privateConversationIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ConversationUser> participants = conversationUserRepository.findByConversationIds(privateConversationIds);
        if (participants == null || participants.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, Long> partnerIdByConversation = new LinkedHashMap<>();
        for (ConversationUser participant : participants) {
            Long conversationId = participant.getConversationId();
            Long memberId = participant.getMemberId();
            if (conversationId == null || memberId == null) {
                continue;
            }
            if (Objects.equals(memberId, currentUserId)) {
                continue;
            }
            partnerIdByConversation.putIfAbsent(conversationId, memberId);
        }
        if (partnerIdByConversation.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> partnerIds = new LinkedHashSet<>(partnerIdByConversation.values());
        Map<Long, User> partnerById = userRepository.getUsersMapByIds(partnerIds);
        if (partnerById == null || partnerById.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, User> result = new LinkedHashMap<>();
        for (Map.Entry<Long, Long> entry : partnerIdByConversation.entrySet()) {
            User partner = partnerById.get(entry.getValue());
            if (partner != null) {
                result.put(entry.getKey(), partner);
            }
        }
        return result;
    }

    private Set<Long> extractConversationIds(List<Conversation> conversations) {
        if (conversations == null || conversations.isEmpty()) {
            return new LinkedHashSet<>();
        }
        return conversations.stream()
            .map(Conversation::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Map<Long, ChatGroup> loadGroupConversationInfo(List<Conversation> groupConversations) {
        if (groupConversations == null || groupConversations.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, Long> groupIdByConversation = new LinkedHashMap<>();
        for (Conversation conversation : groupConversations) {
            Long conversationId = conversation.getId();
            Long groupId = conversation.getGroupId();
            if (conversationId == null || groupId == null) {
                continue;
            }
            groupIdByConversation.putIfAbsent(conversationId, groupId);
        }
        if (groupIdByConversation.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> groupIds = new LinkedHashSet<>(groupIdByConversation.values());
        List<ChatGroup> chatGroups = chatGroupRepository.findByIds(groupIds);
        if (chatGroups == null || chatGroups.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, ChatGroup> chatGroupById = new HashMap<>();
        for (ChatGroup chatGroup : chatGroups) {
            if (chatGroup.getId() == null) {
                continue;
            }
            chatGroupById.putIfAbsent(chatGroup.getId(), chatGroup);
        }
        if (chatGroupById.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, ChatGroup> result = new LinkedHashMap<>();
        for (Map.Entry<Long, Long> entry : groupIdByConversation.entrySet()) {
            ChatGroup chatGroup = chatGroupById.get(entry.getValue());
            if (chatGroup != null) {
                result.put(entry.getKey(), chatGroup);
            }
        }
        return result;
    }




    private static final class ConversationBuckets {
        private final List<Conversation> privateConversations;
        private final List<Conversation> groupConversations;

        private ConversationBuckets(List<Conversation> privateConversations, List<Conversation> groupConversations) {
            this.privateConversations = privateConversations;
            this.groupConversations = groupConversations;
        }

        private List<Conversation> privateConversations() {
            return privateConversations;
        }

        private List<Conversation> groupConversations() {
            return groupConversations;
        }
    }
}
