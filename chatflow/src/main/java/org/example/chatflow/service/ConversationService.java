package org.example.chatflow.service;


import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.vo.SessionVO;

import java.util.List;

/**
 * @author by zzr
 */
public interface ConversationService {
    CurlResponse<List<SessionVO>> getSessionList();
    CurlResponse<String> setFavorite(Long param);

    CurlResponse<String> cancelFavorite(Long param);

    CurlResponse<String> deleteConversation(Long param);

    CurlResponse<Long> restoreByGroup(Long groupId);

    CurlResponse<Long> restoreByFriend(Long friendId);

    /**
     * 为指定的两个用户创建或恢复单聊会话
     * @param userId 当前用户
     * @param friendId 好友用户
     * @return 会话ID
     */
    Long ensurePrivateConversation(Long userId, Long friendId);

    /**
     * 查询双方已存在的单聊会话ID
     * @param userId 当前用户
     * @param friendId 好友用户
     * @return 已存在的会话ID，若不存在返回 null
     */
    Long findExistingPrivateConversation(Long userId, Long friendId);
}
