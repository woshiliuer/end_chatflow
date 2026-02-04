package org.example.chatflow.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.chatflow.model.entity.FavoriteItem;

/**
 * 用户收藏项仓储接口
 */
public interface FavoriteItemRepository extends BaseRepository<FavoriteItem, Long> {

    Page<FavoriteItem> pageByUserId(Long userId, int page, int size, Integer itemType);
}
