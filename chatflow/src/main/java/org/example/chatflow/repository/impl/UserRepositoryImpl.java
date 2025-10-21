package org.example.chatflow.repository.impl;

import org.example.chatflow.mapper.UserMapper;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户仓储实现，集中处理用户实体的数据库交互。
 */
@Repository
public class UserRepositoryImpl extends BaseRepositoryImpl<UserMapper, User, Long> implements UserRepository {

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(lambdaQuery()
            .eq(User::getEmail, email)
            .one());
    }

    @Override
    public boolean existsByEmail(String email) {
        return lambdaQuery()
            .eq(User::getEmail, email)
            .exists();
    }
}
