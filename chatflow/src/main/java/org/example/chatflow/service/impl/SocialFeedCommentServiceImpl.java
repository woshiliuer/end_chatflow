package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.model.entity.SocialFeed;
import org.example.chatflow.model.entity.SocialFeedComment;
import org.example.chatflow.repository.FriendRelationRepository;
import org.example.chatflow.repository.SocialFeedCommentRepository;
import org.example.chatflow.repository.SocialFeedRepository;
import org.example.chatflow.service.SocialFeedCommentService;
import org.example.chatflow.support.CurrentUserAccessor;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SocialFeedCommentServiceImpl implements SocialFeedCommentService {

    private final SocialFeedCommentRepository socialFeedCommentRepository;
    private final SocialFeedRepository socialFeedRepository;
    private final FriendRelationRepository friendRelationRepository;
    private final CurrentUserAccessor currentUserAccessor;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<Long> comment(Long feedId, String content) {
        VerifyUtil.isTrue(feedId == null, ErrorCode.VALIDATION_ERROR);
        String commentContent = StringUtils.trimToNull(content);
        VerifyUtil.isTrue(commentContent == null, ErrorCode.VALIDATION_ERROR);

        Long userId = currentUserAccessor.getCurrentUser().getId();
        VerifyUtil.isTrue(userId == null, ErrorCode.USER_NOT_LOGIN);

        // 获取动态信息
        SocialFeed feed = socialFeedRepository.findById(feedId).orElse(null);
        VerifyUtil.isTrue(feed == null, ErrorCode.BUSINESS_ERROR);

        Long authorId = feed.getCreateUserId();
        VerifyUtil.isTrue(authorId == null, ErrorCode.BUSINESS_ERROR);

        // 验证是否是好友关系（自己评论自己的动态也允许）
        if (!userId.equals(authorId)) {
            // 检查是否是好友
            boolean isFriend = friendRelationRepository.findByUserAndFriendId(userId, authorId) != null;
            VerifyUtil.isTrue(!isFriend, ErrorCode.BUSINESS_ERROR);
        }

        SocialFeedComment comment = new SocialFeedComment();
        comment.setFeedId(feedId);
        comment.setUserId(userId);
        comment.setContent(commentContent);

        VerifyUtil.ensureOperationSucceeded(socialFeedCommentRepository.save(comment), ErrorCode.INTERNAL_ERROR);
        VerifyUtil.isTrue(comment.getId() == null, ErrorCode.INTERNAL_ERROR);
        return CurlResponse.success(comment.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<Boolean> deleteMyComment(Long commentId) {
        VerifyUtil.isTrue(commentId == null, ErrorCode.VALIDATION_ERROR);

        Long userId = currentUserAccessor.getCurrentUser().getId();
        VerifyUtil.isTrue(userId == null, ErrorCode.USER_NOT_LOGIN);

        SocialFeedComment comment = socialFeedCommentRepository.findById(commentId).orElse(null);
        VerifyUtil.isTrue(comment == null, ErrorCode.BUSINESS_ERROR);
        VerifyUtil.isTrue(comment.getUserId() == null || !comment.getUserId().equals(userId), ErrorCode.BUSINESS_ERROR);

        VerifyUtil.ensureOperationSucceeded(socialFeedCommentRepository.deleteById(commentId), ErrorCode.INTERNAL_ERROR);
        return CurlResponse.success(true);
    }
}
