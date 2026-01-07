package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.entity.EmojiPack;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 表情包管理
 */
public interface EmojiPackService {
    Map<Long,EmojiPack> findPackByIds(Set<Long> ids);

    boolean save(EmojiPack emojiPack);

    boolean update(EmojiPack emojiPack);

    CurlResponse<String> delete(Long id);
}
