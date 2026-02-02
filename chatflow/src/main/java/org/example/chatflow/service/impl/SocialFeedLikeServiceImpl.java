package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.common.enums.SocialFeedLikeStatus;
import org.example.chatflow.model.entity.SocialFeedLike;
import org.example.chatflow.repository.SocialFeedLikeRepository;
import org.example.chatflow.service.SocialFeedLikeService;
import org.example.chatflow.support.CurrentUserAccessor;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SocialFeedLikeServiceImpl implements SocialFeedLikeService {

    private final SocialFeedLikeRepository socialFeedLikeRepository;
    private final CurrentUserAccessor currentUserAccessor;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<Boolean> like(Long feedId) {
        VerifyUtil.isTrue(feedId == null, ErrorCode.VALIDATION_ERROR);

        Long userId = currentUserAccessor.getCurrentUser().getId();
        VerifyUtil.isTrue(userId == null, ErrorCode.USER_NOT_LOGIN);

        SocialFeedLike existing = socialFeedLikeRepository
            .findByFeedIdAndUserId(feedId, userId)
            .orElse(null);

        if (existing == null) {
            SocialFeedLike record = new SocialFeedLike();
            record.setFeedId(feedId);
            record.setUserId(userId);
            record.setStatus(SocialFeedLikeStatus.VALID.getCode());
            VerifyUtil.ensureOperationSucceeded(socialFeedLikeRepository.save(record), ErrorCode.INTERNAL_ERROR);
            return CurlResponse.success(true);
        }

        Integer status = existing.getStatus();
        VerifyUtil.isTrue(status == null, ErrorCode.INTERNAL_ERROR);
        VerifyUtil.isTrue(!SocialFeedLikeStatus.CANCELED.getCode().equals(status), ErrorCode.BUSINESS_ERROR);

        existing.setStatus(SocialFeedLikeStatus.VALID.getCode());
        VerifyUtil.ensureOperationSucceeded(socialFeedLikeRepository.update(existing), ErrorCode.INTERNAL_ERROR);
        return CurlResponse.success(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurlResponse<Boolean> unlike(Long feedId) {
        VerifyUtil.isTrue(feedId == null, ErrorCode.VALIDATION_ERROR);

        Long userId = currentUserAccessor.getCurrentUser().getId();
        VerifyUtil.isTrue(userId == null, ErrorCode.USER_NOT_LOGIN);

        SocialFeedLike existing = socialFeedLikeRepository
            .findByFeedIdAndUserId(feedId, userId)
            .orElse(null);

        VerifyUtil.isTrue(existing == null, ErrorCode.BUSINESS_ERROR);

        Integer status = existing.getStatus();
        VerifyUtil.isTrue(status == null, ErrorCode.INTERNAL_ERROR);
        VerifyUtil.isTrue(!SocialFeedLikeStatus.VALID.getCode().equals(status), ErrorCode.BUSINESS_ERROR);

        existing.setStatus(SocialFeedLikeStatus.CANCELED.getCode());
        VerifyUtil.ensureOperationSucceeded(socialFeedLikeRepository.update(existing), ErrorCode.INTERNAL_ERROR);
        return CurlResponse.success(true);
    }
}
