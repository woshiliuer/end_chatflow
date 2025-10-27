package org.example.chatflow.repository;

import org.example.chatflow.model.entity.FriendRequest;

import java.util.List;

/**
 * 好友申请仓储接口
 */
public interface FriendRequestRepository extends BaseRepository<FriendRequest, Long> {

    FriendRequest findByRequesterAndReceiverId(Long requesterId, Long receiverId);

    List<FriendRequest> findByRequesterId(Long userId);

    List<FriendRequest> findByReceiverId(Long userId);
}
