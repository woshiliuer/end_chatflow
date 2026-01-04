package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.entity.UserEmojiItem;
import org.example.chatflow.repository.UserEmojiItemRepository;
import org.example.chatflow.service.UserEmojiItemService;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserEmojiItemServiceImpl implements UserEmojiItemService {

    private final UserEmojiItemRepository userEmojiItemRepository;

    @Override
    public CurlResponse<List<UserEmojiItem>> listByUserId(Long userId) {
        VerifyUtil.isTrue(userId == null, "参数错误");
        return CurlResponse.success(userEmojiItemRepository.findByUserId(userId));
    }

    @Override
    public CurlResponse<String> save(UserEmojiItem relation) {
        VerifyUtil.isTrue(relation == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(userEmojiItemRepository.save(relation), "保存失败");
        return CurlResponse.success("保存成功");
    }

    @Override
    public CurlResponse<String> update(UserEmojiItem relation) {
        VerifyUtil.isTrue(relation == null || relation.getId() == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(userEmojiItemRepository.update(relation), "更新失败");
        return CurlResponse.success("更新成功");
    }

    @Override
    public CurlResponse<String> delete(Long id) {
        VerifyUtil.isTrue(id == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(userEmojiItemRepository.deleteById(id), "删除失败");
        return CurlResponse.success("删除成功");
    }
}
