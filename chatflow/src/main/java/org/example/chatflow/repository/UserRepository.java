package org.example.chatflow.repository;

import org.example.chatflow.model.entity.User;

import java.util.Optional;

/**
 * 用户仓储接口，统一封装用户相关的持久化操作。
 */
public interface UserRepository extends BaseRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email);
}
