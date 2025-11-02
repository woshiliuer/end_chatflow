package org.example.chatflow.service;


import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.vo.SessionVO;

import java.util.List;

/**
 * @author by zzr
 */
public interface ConversationService {
    CurlResponse<List<SessionVO>> getSessionList();

    CurlResponse<String> setConversationCommon(Long conversationId, boolean enable);

    CurlResponse<String> deleteConversation(Long conversationId);
}
