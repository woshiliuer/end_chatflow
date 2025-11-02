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
}
