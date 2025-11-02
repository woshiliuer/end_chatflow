package org.example.chatflow.repository;

import java.util.Collection;
import java.util.List;
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
    List<ConversationUser> findByMemberId(Long memberId);

    /**
     * 根据会话ID集合查询成员关系
     *
     * @param conversationIds 会话ID集合
     * @return 会话成员关系列表
     */
    List<ConversationUser> findByConversationIds(Collection<Long> conversationIds);

    ConversationUser findByConversationIdAndMemberId(Long conversationId, Long memberId);
}
