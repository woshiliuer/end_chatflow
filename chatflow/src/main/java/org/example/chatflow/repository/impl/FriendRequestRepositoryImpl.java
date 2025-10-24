package org.example.chatflow.repository.impl;

import org.example.chatflow.mapper.FriendRequestMapper;
import org.example.chatflow.model.entity.FriendRequest;
import org.example.chatflow.repository.FriendRequestRepository;
import org.springframework.stereotype.Repository;

/**
 * @author by zzr
 */
@Repository
public class FriendRequestRepositoryImpl
        extends BaseRepositoryImpl<FriendRequestMapper, FriendRequest, Long>
        implements FriendRequestRepository {
    @Override
    public FriendRequest findByRequesterAndReceiverId(Long requesterId, Long receiverId) {
        return lambdaQuery().eq(FriendRequest::getRequesterId,requesterId).eq(FriendRequest::getReceiverId,
                receiverId).one();
    }
}
