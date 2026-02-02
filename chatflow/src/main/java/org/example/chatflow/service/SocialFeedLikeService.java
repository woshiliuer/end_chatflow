package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.entity.SocialFeedLike;

/**
 * 社交动态点赞服务
 */
public interface SocialFeedLikeService {

    CurlResponse<Boolean> save(SocialFeedLike like);

    CurlResponse<Boolean> delete(Long id);
}
