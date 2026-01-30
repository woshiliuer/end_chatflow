package org.example.chatflow.repository.impl;

import org.example.chatflow.mapper.UserEmojiItemMapper;
import org.example.chatflow.model.entity.UserEmojiItem;
import org.example.chatflow.repository.UserEmojiItemRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * 用户-表情项关系仓储实现
 */
@Repository
public class UserEmojiItemRepositoryImpl
    extends BaseRepositoryImpl<UserEmojiItemMapper, UserEmojiItem, Long>
    implements UserEmojiItemRepository {

    @Override
    public List<UserEmojiItem> findByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return lambdaQuery()
            .eq(UserEmojiItem::getUserId, userId)
            .orderByAsc(UserEmojiItem::getSort)
            .orderByAsc(UserEmojiItem::getId)
            .list();
    }

    @Override
    public Integer getNextSortValue(Long userId) {
        if (userId == null) {
            return 1; // 默认从1开始
        }

        // 查询最大的 sort 值
        Integer maxSort = lambdaQuery()
                .eq(UserEmojiItem::getUserId, userId)
                .select(UserEmojiItem::getSort)
                .orderByDesc(UserEmojiItem::getSort)
                .last("LIMIT 1")
                .oneOpt()
                .map(UserEmojiItem::getSort)
                .orElse(0); // 如果没找到，默认从0开始

        return maxSort + 1;
    }
}
