package org.example.chatflow.repository.impl;

import org.example.chatflow.mapper.ChatGroupMapper;
import org.example.chatflow.model.entity.ChatGroup;
import org.example.chatflow.repository.ChatGroupRepository;
import org.springframework.stereotype.Repository;

/**
 * 群聊仓储实现
 */
@Repository
public class ChatGroupRepositoryImpl
    extends BaseRepositoryImpl<ChatGroupMapper, ChatGroup, Long>
    implements ChatGroupRepository {
}

