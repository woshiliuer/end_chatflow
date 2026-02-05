package org.example.chatflow.repository;

import org.example.chatflow.model.entity.SocialFeedComment;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 社交动态评论仓储接口
 */
public interface SocialFeedCommentRepository extends BaseRepository<SocialFeedComment, Long> {

    Map<Long, Long> countByFeedIds(Collection<Long> feedIds);

    List<SocialFeedComment> findByFeedId(Long feedId);

    boolean deleteByFeedId(Long feedId);
}
