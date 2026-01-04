package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.entity.EmojiPack;
import org.example.chatflow.repository.EmojiPackRepository;
import org.example.chatflow.service.EmojiPackService;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmojiPackServiceImpl implements EmojiPackService {

    private final EmojiPackRepository emojiPackRepository;

    @Override
    public CurlResponse<List<EmojiPack>> listAll() {
        return CurlResponse.success(emojiPackRepository.findAll());
    }

    @Override
    public CurlResponse<EmojiPack> detail(Long id) {
        VerifyUtil.isTrue(id == null, "参数错误");
        EmojiPack emojiPack = emojiPackRepository.findById(id).orElse(null);
        VerifyUtil.isTrue(emojiPack == null, "表情包不存在");
        return CurlResponse.success(emojiPack);
    }

    @Override
    public boolean save(EmojiPack emojiPack) {
        return emojiPackRepository.save(emojiPack);
    }

    @Override
    public boolean update(EmojiPack emojiPack) {
        return emojiPackRepository.update(emojiPack);
    }

    @Override
    public CurlResponse<String> delete(Long id) {
        VerifyUtil.isTrue(id == null, "参数错误");
        VerifyUtil.ensureOperationSucceeded(emojiPackRepository.deleteById(id), "删除失败");
        return CurlResponse.success("删除成功");
    }
}
