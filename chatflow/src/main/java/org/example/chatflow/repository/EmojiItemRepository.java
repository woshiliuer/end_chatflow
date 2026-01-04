package org.example.chatflow.repository;

import org.example.chatflow.model.entity.EmojiItem;

import java.util.List;

/**
 * 表情项仓储接口
 */
public interface EmojiItemRepository extends BaseRepository<EmojiItem, Long> {

    List<EmojiItem> findByPackId(Long packId);
}
