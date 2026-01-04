package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.entity.EmojiItem;

import java.util.List;

/**
 * 表情项管理
 */
public interface EmojiItemService {

    CurlResponse<List<EmojiItem>> listByPackId(Long packId);

    CurlResponse<EmojiItem> detail(Long id);

    CurlResponse<String> save(EmojiItem emojiItem);

    CurlResponse<String> update(EmojiItem emojiItem);

    CurlResponse<String> delete(Long id);
}
