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
            .orderByAsc(UserEmojiPack::getCreateTime)
            .list();
    }

    @Override
    public Integer getNextSortValue(Long userId) {
        if (userId == null) {
            return 1;
        }

        Integer maxSort = lambdaQuery()
            .eq(UserEmojiPack::getUserId, userId)
            .select(UserEmojiPack::getSort)
            .orderByDesc(UserEmojiPack::getSort)
            .last("LIMIT 1")
            .oneOpt()
            .map(UserEmojiPack::getSort)
            .orElse(0);

        return maxSort + 1;
    }

    @Override
    public boolean deleteByUserIdAndPackId(Long userId, Long packId) {
        if (userId == null || packId == null) {
            return false;
        }
        return lambdaUpdate()
            .eq(UserEmojiPack::getUserId, userId)
            .eq(UserEmojiPack::getPackId, packId)
            .remove();
    }
}
