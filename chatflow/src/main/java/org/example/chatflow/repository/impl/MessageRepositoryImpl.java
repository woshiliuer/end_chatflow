package org.example.chatflow.repository.impl;

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
}
