package org.example.chatflow.repository.impl;

import org.example.chatflow.mapper.UserEmojiPackMapper;
import org.example.chatflow.model.entity.UserEmojiPack;
import org.example.chatflow.repository.UserEmojiPackRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * 用户-表情包关系仓储实现
 */
@Repository
public class UserEmojiPackRepositoryImpl
    extends BaseRepositoryImpl<UserEmojiPackMapper, UserEmojiPack, Long>
    implements UserEmojiPackRepository {

    @Override
    public List<UserEmojiPack> findByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return lambdaQuery()
            .eq(UserEmojiPack::getUserId, userId)
            .orderByAsc(UserEmojiPack::getSort)
            .orderByAsc(UserEmojiPack::getId)
            .list();
    }
}
