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
}
