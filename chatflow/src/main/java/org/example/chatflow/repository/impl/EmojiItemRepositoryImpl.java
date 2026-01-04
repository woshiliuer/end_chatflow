package org.example.chatflow.repository.impl;

import org.example.chatflow.mapper.EmojiItemMapper;
import org.example.chatflow.model.entity.EmojiItem;
import org.example.chatflow.repository.EmojiItemRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * 表情项仓储实现
 */
@Repository
public class EmojiItemRepositoryImpl
    extends BaseRepositoryImpl<EmojiItemMapper, EmojiItem, Long>
    implements EmojiItemRepository {

    @Override
    public List<EmojiItem> findByPackId(Long packId) {
        if (packId == null) {
            return Collections.emptyList();
        }
        return lambdaQuery()
            .eq(EmojiItem::getPackId, packId)
            .orderByAsc(EmojiItem::getId)
            .list();
    }
}
