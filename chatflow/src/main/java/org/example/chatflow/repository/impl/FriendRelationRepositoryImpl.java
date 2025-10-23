package org.example.chatflow.repository.impl;

import org.example.chatflow.mapper.FriendRelationMapper;
import org.example.chatflow.mapper.UserMapper;
import org.example.chatflow.model.entity.FriendRelation;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.repository.FriendRelationRepository;
import org.example.chatflow.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author by zzr
 */
@Repository
public class FriendRelationRepositoryImpl extends BaseRepositoryImpl<FriendRelationMapper, FriendRelation, Long> implements FriendRelationRepository {
    @Override
    public List<FriendRelation>  getFriendRelationByFriendId(Long friendId) {
        return lambdaQuery().eq(FriendRelation::getFriendId, friendId).list();
    }

    @Override
    public List<FriendRelation> getFriendRelationByUserId(Long userId) {
        return lambdaQuery().eq(FriendRelation::getUserId, userId).list();
    }
}
