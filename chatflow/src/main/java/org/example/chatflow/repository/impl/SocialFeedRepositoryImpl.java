package org.example.chatflow.repository.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.example.chatflow.mapper.SocialFeedMapper;
import org.example.chatflow.model.entity.SocialFeed;
import org.example.chatflow.repository.SocialFeedRepository;
import org.springframework.stereotype.Repository;

@Repository
public class SocialFeedRepositoryImpl
    extends BaseRepositoryImpl<SocialFeedMapper, SocialFeed, Long>
    implements SocialFeedRepository {

    @Override
    public Page<SocialFeed> pageByContent(Integer page, Integer size, String content) {
        int pageNum = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 10 : size;
        String kw = StringUtils.trimToNull(content);
        return lambdaQuery()
            .like(kw != null, SocialFeed::getContent, kw)
            .orderByDesc(SocialFeed::getId)
            .page(new Page<>(pageNum, pageSize));
    }
}
