package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.entity.EmojiItem;
import org.example.chatflow.repository.EmojiItemRepository;
import org.example.chatflow.service.EmojiItemService;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmojiItemServiceImpl implements EmojiItemService {

    private final EmojiItemRepository emojiItemRepository;

    @Override
    public CurlResponse<List<EmojiItem>> listByPackId(Long packId) {
        VerifyUtil.isTrue(packId == null, "参数错误");
        return CurlResponse.success(emojiItemRepository.findByPackId(packId));
    }

    @Override
    public CurlResponse<EmojiItem> detail(Long id) {
        VerifyUtil.isTrue(id == null, "参数错误");
        EmojiItem emojiItem = emojiItemRepository.findById(id).orElse(null);
        VerifyUtil.isTrue(emojiItem == null, "表情项不存在");
        return CurlResponse.success(emojiItem);
    }

    @Override
    public boolean save(EmojiItem emojiItem) {
        return emojiItemRepository.save(emojiItem);
    }

    @Override
    public boolean saveBatch(List<EmojiItem> emojiItems) {
        return emojiItemRepository.saveBatch(emojiItems);
    }

    @Override
    public CurlResponse<String> update(EmojiItem emojiItem) {
        VerifyUtil.isTrue(emojiItem == null || emojiItem.getId() == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(emojiItemRepository.update(emojiItem), "更新失败");
        return CurlResponse.success("更新成功");
    }

    @Override
    public CurlResponse<String> delete(Long id) {
        VerifyUtil.isTrue(id == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(emojiItemRepository.deleteById(id), "删除失败");
        return CurlResponse.success("删除成功");
    }
}
