package org.example.chatflow.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.chatflow.model.entity.SocialFeed;

/**
 * 社交动态仓储接口
 */
public interface SocialFeedRepository extends BaseRepository<SocialFeed, Long> {

    Page<SocialFeed> pageByContent(Integer page, Integer size, String content);
}
