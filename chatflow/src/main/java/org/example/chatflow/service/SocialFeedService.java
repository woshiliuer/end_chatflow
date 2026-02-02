package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.entity.SocialFeed;

/**
 * 社交动态服务
 */
public interface SocialFeedService {

    CurlResponse<Boolean> save(SocialFeed feed);

    CurlResponse<Boolean> update(SocialFeed feed);

    CurlResponse<Boolean> delete(Long id);
}
