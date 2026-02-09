package org.example.chatflow.repository.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.example.chatflow.common.enums.ConversationStatus;
import org.example.chatflow.mapper.ConversationUserMapper;
import org.example.chatflow.model.entity.ConversationUser;
import org.example.chatflow.repository.ConversationUserRepository;
import org.springframework.stereotype.Repository;

/**
 * Conversation-user relation repository implementation.
 */
@Repository
public class ConversationUserRepositoryImpl
        extends BaseRepositoryImpl<ConversationUserMapper, ConversationUser, Long>
        implements ConversationUserRepository {

    @Override
    public List<ConversationUser> findByMemberId(Long memberId) {
        if (memberId == null) {
            return Collections.emptyList();
        }
        return lambdaQuery()
            .eq(ConversationUser::getMemberId, memberId)
            .ne(ConversationUser::getStatus, ConversationStatus.HIDDEN.getCode())
            .list();
    }

    @Override
    public List<ConversationUser> findAllByMemberId(Long memberId) {
        if (memberId == null) {
            return Collections.emptyList();
        }
        return lambdaQuery()
            .eq(ConversationUser::getMemberId, memberId)
            .list();
    }

    @Override
    public List<ConversationUser> findByConversationIds(Collection<Long> conversationIds) {
        if (conversationIds == null || conversationIds.isEmpty()) {
            return Collections.emptyList();
        }
        return lambdaQuery()
            .in(ConversationUser::getConversationId, conversationIds)
            .list();
    }



    @Override
    public ConversationUser findByConversationIdAndMemberId(Long conversationId, Long memberId) {
        if (conversationId == null || memberId == null) {
            return null;
        }
        return lambdaQuery()
                .eq(ConversationUser::getConversationId, conversationId)
                .eq(ConversationUser::getMemberId, memberId)
                .one();
    }

    @Override
    public ConversationUser findReceiverId(Long conversationId, Long senderId) {
        if (conversationId == null || senderId == null) {
            return null;
        }
        return lambdaQuery()
                .eq(ConversationUser::getConversationId, conversationId)
                .ne(ConversationUser::getMemberId, senderId)
                .one();
    }
}
