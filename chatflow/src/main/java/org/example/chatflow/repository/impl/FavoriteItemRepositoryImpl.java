package org.example.chatflow.repository.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.chatflow.mapper.FavoriteItemMapper;
import org.example.chatflow.model.entity.FavoriteItem;
import org.example.chatflow.repository.FavoriteItemRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class FavoriteItemRepositoryImpl
    extends BaseRepositoryImpl<FavoriteItemMapper, FavoriteItem, Long>
    implements FavoriteItemRepository {

    @Override
    public Page<FavoriteItem> pageByUserId(Long userId, int page, int size, Integer itemType) {
        if (userId == null) {
            return new Page<>(page, size);
        }
        Page<FavoriteItem> p = new Page<>(page, size);
        return lambdaQuery()
            .eq(FavoriteItem::getUserId, userId)
            .eq(itemType != null, FavoriteItem::getItemType, itemType)
            .orderByDesc(FavoriteItem::getCreateTime)
            .page(p);
    }

    @Override
    public List<FavoriteItem> listByUserId(Long userId, Integer itemType) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return lambdaQuery()
            .eq(FavoriteItem::getUserId, userId)
            .eq(itemType != null, FavoriteItem::getItemType, itemType)
            .orderByDesc(FavoriteItem::getCreateTime)
            .list();
    }
}
