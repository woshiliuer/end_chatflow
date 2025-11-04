package org.example.chatflow.repository.impl;

import org.example.chatflow.common.enums.Deleted;
import org.example.chatflow.mapper.ConversationMapper;
import org.example.chatflow.model.entity.Conversation;
import org.example.chatflow.repository.ConversationRepository;
import org.springframework.stereotype.Repository;

/**
 * Conversation repository implementation.
 */
@Repository
public class ConversationRepositoryImpl
        extends BaseRepositoryImpl<ConversationMapper, Conversation, Long>
        implements ConversationRepository {

    @Override
    public Conversation findByGroupId(Long groupId) {
        return lambdaQuery()
                .eq(Conversation::getGroupId, groupId)
                .eq(Conversation::getDeleted, Deleted.HAS_NOT_DELETED.getCode())
                .one();
    }
}
