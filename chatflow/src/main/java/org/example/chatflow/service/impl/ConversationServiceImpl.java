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
import org.example.chatflow.common.constants.OssConstant;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.ConversationType;
import org.example.chatflow.common.enums.ConversationStatus;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.model.entity.ChatGroup;
import org.example.chatflow.model.entity.Conversation;
import org.example.chatflow.model.entity.ConversationUser;
import org.example.chatflow.model.entity.Message;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.model.vo.SessionVO;
import org.example.chatflow.repository.ChatGroupRepository;
import org.example.chatflow.repository.ConversationRepository;
import org.example.chatflow.repository.ConversationUserRepository;
import org.example.chatflow.repository.MessageRepository;
import org.example.chatflow.repository.UserRepository;
import org.example.chatflow.service.ConversationService;
import org.example.chatflow.support.CurrentUserAccessor;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private final ChatGroupRepository chatGroupRepository;
    private final MessageRepository messageRepository;
    private final CurrentUserAccessor currentUserAccessor;

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

        // 拉取消息明细，用于补全最新消息和未读统计
        Map<Long, List<Message>> messagesByConversation = loadMessagesByConversationIds(conversationIds);
        Map<Long, Message> lastMessageMap = buildLastMessageMap(conversations, messagesByConversation);

        Map<Long, Integer> unreadCountMap = calculateUnreadCounts(
            messagesByConversation, lastReadSeqByConversation, user.getId());

        List<SessionVO> sessionVOList = buildSessionList(conversations,
            privateConversationUserMap, groupConversationMap, lastMessageMap, unreadCountMap, statusByConversation);

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
        if (!Objects.equals(conversationUser.getStatus(), ConversationStatus.FAVORITE.getCode())) {
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
                                             Map<Long, Integer> statusByConversation) {
        // 逐个会话组装输出字段，重用预先拉好的数据 
        List<SessionVO> sessionVOList = new ArrayList<>(conversations.size());
        for (Conversation conversation : conversations) {
            Message message = lastMessageMap.get(conversation.getId());
            int unreadCount = unreadCountMap.getOrDefault(conversation.getId(), 0);
            int status = statusByConversation.getOrDefault(conversation.getId(), ConversationStatus.NORMAL.getCode());
            SessionVO sessionVO = SessionVO.SessionVOMapper.INSTANCE.toVO(conversation, message, unreadCount, status);
            if (ConversationType.PRIVATE.getCode().equals(conversation.getConversationType())) {
                User partner = privateConversationUserMap.get(conversation.getId());
                if (partner != null) {
                    sessionVO.setDisplayName(partner.getNickname());
                    sessionVO.setAvatarFullUrl(OssConstant.buildFullUrl(partner.getAvatarUrl()));
                }
            } else if (ConversationType.GROUP.getCode().equals(conversation.getConversationType())) {
                ChatGroup chatGroup = groupConversationMap.get(conversation.getId());
                if (chatGroup != null) {
                    sessionVO.setDisplayName(chatGroup.getGroupName());
                    sessionVO.setAvatarFullUrl(OssConstant.buildFullUrl(chatGroup.getGroupAvatarUrl()));
                }
            }
            sessionVOList.add(sessionVO);
        }
        return sessionVOList;
    }

    private Map<Long, List<Message>> loadMessagesByConversationIds(Set<Long> conversationIds) {
        if (conversationIds == null || conversationIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Message> messages = messageRepository.findByConversationIds(conversationIds);
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
        Map<Long, Message> messageById = new LinkedHashMap<>();
        messagesByConversation.values().forEach(messageList ->
            messageList.stream()
                .filter(message -> message.getId() != null)
                .forEach(message -> messageById.putIfAbsent(message.getId(), message))
        );

        Map<Long, Message> result = new LinkedHashMap<>();
        for (Conversation conversation : conversations) {
            Long conversationId = conversation.getId();
            if (conversationId == null) {
                continue;
            }
            Message lastMessage = null;
            Long lastMessageId = conversation.getLastMessageId();
            if (lastMessageId != null) {
                lastMessage = messageById.get(lastMessageId);
            }
            if (lastMessage == null) {
                List<Message> messageList = messagesByConversation.get(conversationId);
                if (messageList != null && !messageList.isEmpty()) {
                    lastMessage = messageList.get(messageList.size() - 1);
                }
            }
            if (lastMessage != null) {
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
