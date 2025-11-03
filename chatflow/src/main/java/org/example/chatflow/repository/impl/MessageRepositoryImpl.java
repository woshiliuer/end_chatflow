package org.example.chatflow.repository.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.example.chatflow.mapper.MessageMapper;
import org.example.chatflow.model.entity.Message;
import org.example.chatflow.repository.MessageRepository;
import org.springframework.stereotype.Repository;

/**
 * Message repository implementation.
 */
@Repository
public class MessageRepositoryImpl
        extends BaseRepositoryImpl<MessageMapper, Message, Long>
        implements MessageRepository {

    @Override
    public List<Message> findByConversationIds(Collection<Long> conversationIds) {
        if (conversationIds == null || conversationIds.isEmpty()) {
            return Collections.emptyList();
        }
        return lambdaQuery()
            .in(Message::getConversationId, conversationIds)
            .orderByAsc(Message::getConversationId)
            .orderByAsc(Message::getSequence)
            .list();
    }

    @Override
    public void deleteByConversationId(Long conversationId) {
        if (conversationId == null) {
            return;
        }
        lambdaUpdate()
            .eq(Message::getConversationId, conversationId)
            .remove();
    }
    
    @Override
    public Long getMaxSequenceByConversationId(Long conversationId) {
        if (conversationId == null) {
            return 0L;
        }
        Message message = lambdaQuery()
            .eq(Message::getConversationId, conversationId)
            .orderByDesc(Message::getSequence)
            .last("LIMIT 1")
            .one();
        return message != null ? message.getSequence() : 0L;
    }
}
