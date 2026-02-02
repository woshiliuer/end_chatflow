package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.model.entity.SocialFeedComment;
import org.example.chatflow.repository.SocialFeedCommentRepository;
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
    private final CurrentUserAccessor currentUserAccessor;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<Long> comment(Long feedId, String content) {
        VerifyUtil.isTrue(feedId == null, ErrorCode.VALIDATION_ERROR);
        String commentContent = StringUtils.trimToNull(content);
        VerifyUtil.isTrue(commentContent == null, ErrorCode.VALIDATION_ERROR);

        Long userId = currentUserAccessor.getCurrentUser().getId();
        VerifyUtil.isTrue(userId == null, ErrorCode.USER_NOT_LOGIN);

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
