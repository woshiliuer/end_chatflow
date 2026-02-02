package org.example.chatflow.repository;

import org.example.chatflow.model.entity.SocialFeedLike;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * 社交动态点赞仓储接口
 */
public interface SocialFeedLikeRepository extends BaseRepository<SocialFeedLike, Long> {

    Optional<SocialFeedLike> findByFeedIdAndUserId(Long feedId, Long userId);

    Map<Long, Long> countValidByFeedIds(Collection<Long> feedIds);
}
