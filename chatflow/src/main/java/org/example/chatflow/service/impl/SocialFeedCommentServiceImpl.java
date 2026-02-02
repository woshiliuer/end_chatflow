package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.entity.SocialFeedComment;
import org.example.chatflow.repository.SocialFeedCommentRepository;
import org.example.chatflow.service.SocialFeedCommentService;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SocialFeedCommentServiceImpl implements SocialFeedCommentService {

    private final SocialFeedCommentRepository socialFeedCommentRepository;

    @Override
    public CurlResponse<Boolean> save(SocialFeedComment comment) {
        VerifyUtil.isTrue(comment == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(socialFeedCommentRepository.save(comment), "保存失败");
        return CurlResponse.success(true);
    }

    @Override
    public CurlResponse<Boolean> update(SocialFeedComment comment) {
        VerifyUtil.isTrue(comment == null || comment.getId() == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(socialFeedCommentRepository.update(comment), "更新失败");
        return CurlResponse.success(true);
    }

    @Override
    public CurlResponse<Boolean> delete(Long id) {
        VerifyUtil.isTrue(id == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(socialFeedCommentRepository.deleteById(id), "删除失败");
        return CurlResponse.success(true);
    }
}
