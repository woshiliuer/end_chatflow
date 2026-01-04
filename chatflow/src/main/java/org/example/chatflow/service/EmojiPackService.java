package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.entity.EmojiPack;

import java.util.List;

/**
 * 表情包管理
 */
public interface EmojiPackService {

    CurlResponse<List<EmojiPack>> listAll();

    CurlResponse<EmojiPack> detail(Long id);

    boolean save(EmojiPack emojiPack);

    boolean update(EmojiPack emojiPack);

    CurlResponse<String> delete(Long id);
}
