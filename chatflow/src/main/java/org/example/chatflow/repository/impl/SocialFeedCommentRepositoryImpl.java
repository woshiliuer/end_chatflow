package org.example.chatflow.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.chatflow.mapper.SocialFeedCommentMapper;
import org.example.chatflow.model.entity.SocialFeedComment;
import org.example.chatflow.repository.SocialFeedCommentRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SocialFeedCommentRepositoryImpl
    extends BaseRepositoryImpl<SocialFeedCommentMapper, SocialFeedComment, Long>
    implements SocialFeedCommentRepository {

    @Override
    public boolean deleteByFeedId(Long feedId) {
        if (feedId == null) {
            return false;
        }
        return lambdaUpdate()
                .eq(SocialFeedComment::getFeedId, feedId)
                .remove();
    }

    @Override
    public Map<Long, Long> countByFeedIds(Collection<Long> feedIds) {
        if (feedIds == null || feedIds.isEmpty()) {
            return new LinkedHashMap<>();
        }

        QueryWrapper<SocialFeedComment> qw = new QueryWrapper<>();
        qw.select("feed_id", "COUNT(*) AS cnt")
            .eq("deleted", 0)
            .in("feed_id", feedIds)
            .groupBy("feed_id");

        List<Map<String, Object>> rows = baseMapper.selectMaps(qw);
        Map<Long, Long> result = new LinkedHashMap<>();
        if (rows == null || rows.isEmpty()) {
            return result;
        }
        for (Map<String, Object> row : rows) {
            Object feedIdObj = row.get("feed_id");
            Object cntObj = row.get("cnt");
            if (feedIdObj instanceof Number feedIdNum && cntObj instanceof Number cntNum) {
                result.put(feedIdNum.longValue(), cntNum.longValue());
            }
        }
        return result;
    }

    @Override
    public List<SocialFeedComment> findByFeedId(Long feedId) {
        if (feedId == null) {
            return Collections.emptyList();
        }
        return lambdaQuery()
            .eq(SocialFeedComment::getFeedId, feedId)
            .orderByDesc(SocialFeedComment::getCreateTime)
            .list();
    }
}
