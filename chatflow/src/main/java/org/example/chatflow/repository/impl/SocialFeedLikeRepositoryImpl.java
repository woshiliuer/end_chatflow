package org.example.chatflow.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.chatflow.common.enums.SocialFeedLikeStatus;
import org.example.chatflow.mapper.SocialFeedLikeMapper;
import org.example.chatflow.model.entity.SocialFeedLike;
import org.example.chatflow.repository.SocialFeedLikeRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SocialFeedLikeRepositoryImpl
    extends BaseRepositoryImpl<SocialFeedLikeMapper, SocialFeedLike, Long>
    implements SocialFeedLikeRepository {

    @Override
    public Optional<SocialFeedLike> findByFeedIdAndUserId(Long feedId, Long userId) {
        if (feedId == null || userId == null) {
            return Optional.empty();
        }
        return lambdaQuery()
            .eq(SocialFeedLike::getFeedId, feedId)
            .eq(SocialFeedLike::getUserId, userId)
            .oneOpt();
    }

    @Override
    public Map<Long, Long> countValidByFeedIds(Collection<Long> feedIds) {
        if (feedIds == null || feedIds.isEmpty()) {
            return new LinkedHashMap<>();
        }

        QueryWrapper<SocialFeedLike> qw = new QueryWrapper<>();
        qw.select("feed_id", "COUNT(*) AS cnt")
            .eq("deleted", 0)
            .eq("status", SocialFeedLikeStatus.VALID.getCode())
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
}
