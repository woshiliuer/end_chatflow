package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.entity.UserEmojiPack;
import org.example.chatflow.repository.UserEmojiPackRepository;
import org.example.chatflow.service.UserEmojiPackService;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserEmojiPackServiceImpl implements UserEmojiPackService {

    private final UserEmojiPackRepository userEmojiPackRepository;

    @Override
    public List<UserEmojiPack> listByUserId(Long userId) {
        return userEmojiPackRepository.findByUserId(userId);
    }

    @Override
    public CurlResponse<String> save(UserEmojiPack relation) {
        VerifyUtil.isTrue(relation == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(userEmojiPackRepository.save(relation), "保存失败");
        return CurlResponse.success("保存成功");
    }

    @Override
    public CurlResponse<String> update(UserEmojiPack relation) {
        VerifyUtil.isTrue(relation == null || relation.getId() == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(userEmojiPackRepository.update(relation), "更新失败");
        return CurlResponse.success("更新成功");
    }

    @Override
    public CurlResponse<String> delete(Long id) {
        VerifyUtil.isTrue(id == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(userEmojiPackRepository.deleteById(id), "删除失败");
        return CurlResponse.success("删除成功");
    }
}
