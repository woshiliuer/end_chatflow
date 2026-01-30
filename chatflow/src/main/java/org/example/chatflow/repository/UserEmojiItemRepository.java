package org.example.chatflow.repository;

import org.example.chatflow.model.entity.UserEmojiItem;

import java.util.List;

/**
 * 用户-表情项关系仓储接口
 */
public interface UserEmojiItemRepository extends BaseRepository<UserEmojiItem, Long> {

    List<UserEmojiItem> findByUserId(Long userId);

    Integer getNextSortValue(Long userId);
}
