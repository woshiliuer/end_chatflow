package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.repository.SocialFeedRepository;
import org.example.chatflow.model.entity.SocialFeed;
import org.example.chatflow.service.SocialFeedService;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SocialFeedServiceImpl implements SocialFeedService {

    private final SocialFeedRepository socialFeedRepository;

    @Override
    public CurlResponse<Boolean> save(SocialFeed feed) {
        VerifyUtil.isTrue(feed == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(socialFeedRepository.save(feed), "保存失败");
        return CurlResponse.success(true);
    }

    @Override
    public CurlResponse<Boolean> update(SocialFeed feed) {
        VerifyUtil.isTrue(feed == null || feed.getId() == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(socialFeedRepository.update(feed), "更新失败");
        return CurlResponse.success(true);
    }

    @Override
    public CurlResponse<Boolean> delete(Long id) {
        VerifyUtil.isTrue(id == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(socialFeedRepository.deleteById(id), "删除失败");
        return CurlResponse.success(true);
    }
}
