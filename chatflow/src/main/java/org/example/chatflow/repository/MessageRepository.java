package org.example.chatflow.repository;

import java.util.Collection;
import java.util.List;
import org.example.chatflow.model.entity.Message;

/**
 * Message repository abstraction.
 */
public interface MessageRepository extends BaseRepository<Message, Long> {

    /**
     * 根据会话ID集合查询消息列表，并支持根据起点序号过滤。
     *
     * @param conversationIds 会话ID集合
     * @param visibleSeq      最小可见序号（仅返回 seq > visibleSeq 的消息）
     * @return 消息列表
     */
    List<Message> findByConversationIds(Collection<Long> conversationIds, Long visibleSeq);

    /**
     * 根据会话ID删除消息。
     *
     * @param conversationId 会话ID
     */
    void deleteByConversationId(Long conversationId);
    
    /**
     * 获取会话中的最新消息序号
     *
     * @param conversationId 会话ID
     * @return 最新序号，如果没有消息则返回0
     */
    Long getMaxSequenceByConversationId(Long conversationId);
}
