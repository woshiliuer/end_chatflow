package org.example.chatflow.repository;

import org.example.chatflow.model.entity.Conversation;

/**
 * Conversation repository abstraction.
 */
public interface ConversationRepository extends BaseRepository<Conversation, Long> {

    /**
     * 根据群聊 ID 查询会话
     *
     * @param groupId 群聊 ID
     * @return 会话信息
     */
    Conversation findByGroupId(Long groupId);
}
