package org.example.chatflow.repository;

import org.example.chatflow.model.entity.FriendRelation;

import java.util.List;

/**
 * @author by zzr
 */
public interface FriendRelationRepository extends BaseRepository<FriendRelation,Long>{
    List<FriendRelation> getFriendRelationByFriendId(Long friendId);

    List<FriendRelation>  getFriendRelationByUserId(Long userId);

    FriendRelation findByUserAndFriendId(Long id, Long id1);

    int countFriendRelationByUserIdAndFriends(Long userId, List<Long> friendIds);
}
