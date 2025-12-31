package org.example.chatflow.repository;

import org.example.chatflow.model.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户仓储接口，统一封装用户相关的持久化操作。
 */
public interface UserRepository extends BaseRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    Map<Long, User> getUsersMapByIds(Set<Long> allFriendIds);

    List<User> findExistByIds(Set<Long> memberIdList);

    Map<Long, User> findUserMapByIds(Set<Long> userIds);
}
