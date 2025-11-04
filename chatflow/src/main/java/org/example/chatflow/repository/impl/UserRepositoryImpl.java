package org.example.chatflow.repository.impl;

import io.jsonwebtoken.lang.Collections;
import org.example.chatflow.common.enums.Deleted;
import org.example.chatflow.mapper.UserMapper;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户仓储实现，集中处理用户实体的数据库交互。
 */
@Repository
public class UserRepositoryImpl extends BaseRepositoryImpl<UserMapper, User, Long> implements UserRepository {

    @Override
    public User findByEmail(String email) {
        return lambdaQuery()
            .eq(User::getEmail, email).one();
    }

    @Override
    public boolean existsByEmail(String email) {
        return lambdaQuery()
            .eq(User::getEmail, email)
            .exists();
    }

    @Override
    public Map<Long, User> getUsersMapByIds(Set<Long> allFriendIds) {
        if (allFriendIds == null || allFriendIds.isEmpty()) {
            return Map.of();
        }
        List<User> userList = lambdaQuery().in(User::getId, allFriendIds).list();
        if (userList == null || userList.isEmpty()) {
            return Map.of();
        }
        return userList.stream()
            .collect(Collectors.toMap(User::getId, user -> user, (existing, replacement) -> existing));
    }

    @Override
    public List<User> findExistByIds(Set<Long> memberIdList) {
        if (memberIdList == null || memberIdList.isEmpty()) {
            return new ArrayList<>();
        }
        return lambdaQuery().in(User::getId, memberIdList)
                .eq(User::getDeleted, Deleted.HAS_NOT_DELETED.getCode())
                .list();
    }
}
