package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.vo.GetFriendListVO;

import java.util.List;

/**
 * @author by zzr
 */
public interface FriendService {
    CurlResponse<List<GetFriendListVO>> getFriends();

    CurlResponse<String> addFriendRequest(Long param);
}
