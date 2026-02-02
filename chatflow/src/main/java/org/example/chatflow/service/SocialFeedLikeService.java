package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;

/**
 * 社交动态点赞服务
 */
public interface SocialFeedLikeService {

    CurlResponse<Boolean> like(Long feedId);

    CurlResponse<Boolean> unlike(Long feedId);
}
