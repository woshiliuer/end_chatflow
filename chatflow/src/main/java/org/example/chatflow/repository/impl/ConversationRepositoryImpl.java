package org.example.chatflow.repository.impl;

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
}
