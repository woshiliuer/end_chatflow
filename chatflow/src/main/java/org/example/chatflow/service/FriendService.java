package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.entity.Param;
import org.example.chatflow.model.dto.friend.AddRequestDTO;
import org.example.chatflow.model.dto.friend.AgreeRequestDTO;
import org.example.chatflow.model.vo.GetFriendListVO;

import java.util.List;

/**
 * @author by zzr
 */
public interface FriendService {
    CurlResponse<List<GetFriendListVO>> getFriends();

    CurlResponse<String> addFriendRequest(AddRequestDTO dto);

    CurlResponse<String> agreeFriendRequest(AgreeRequestDTO dto);

    CurlResponse<String> disagreeFriendRequest(Param<Long> param);
}
