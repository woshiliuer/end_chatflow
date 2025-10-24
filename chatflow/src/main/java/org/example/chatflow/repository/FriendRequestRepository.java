package org.example.chatflow.repository;

import org.example.chatflow.model.entity.FriendRequest;

/**
 * 好友申请仓储接口
 */
public interface FriendRequestRepository extends BaseRepository<FriendRequest, Long> {

    FriendRequest findByRequesterAndReceiverId(Long requesterId, Long receiverId);
}
