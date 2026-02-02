package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.entity.SocialFeedComment;

/**
 * 社交动态评论服务
 */
public interface SocialFeedCommentService {

    CurlResponse<Boolean> save(SocialFeedComment comment);

    CurlResponse<Boolean> update(SocialFeedComment comment);

    CurlResponse<Boolean> delete(Long id);
}
