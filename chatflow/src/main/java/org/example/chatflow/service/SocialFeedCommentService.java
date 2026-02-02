package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;

/**
 * 社交动态评论服务
 */
public interface SocialFeedCommentService {

    CurlResponse<Long> comment(Long feedId, String content);

    CurlResponse<Boolean> deleteMyComment(Long commentId);
}
