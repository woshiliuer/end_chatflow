package org.example.chatflow.repository;

import org.example.chatflow.model.entity.ConversationUser;

/**
 * Conversation-user relation repository abstraction.
 */
public interface ConversationUserRepository extends BaseRepository<ConversationUser, Long> {

    /**
     * 查询指定成员参与的所有会话关系
     *
     * @param memberId 成员ID
     * @return 会话关系列表
     */
    java.util.List<ConversationUser> findByMemberId(Long memberId);
}
