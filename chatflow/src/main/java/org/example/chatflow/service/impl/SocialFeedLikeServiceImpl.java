package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.entity.SocialFeedLike;
import org.example.chatflow.repository.SocialFeedLikeRepository;
import org.example.chatflow.service.SocialFeedLikeService;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SocialFeedLikeServiceImpl implements SocialFeedLikeService {

    private final SocialFeedLikeRepository socialFeedLikeRepository;

    @Override
    public CurlResponse<Boolean> save(SocialFeedLike like) {
        VerifyUtil.isTrue(like == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(socialFeedLikeRepository.save(like), "保存失败");
        return CurlResponse.success(true);
    }

    @Override
    public CurlResponse<Boolean> delete(Long id) {
        VerifyUtil.isTrue(id == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(socialFeedLikeRepository.deleteById(id), "删除失败");
        return CurlResponse.success(true);
    }
}
