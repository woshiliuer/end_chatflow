package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.entity.UserEmojiItem;

import java.util.List;

/**
 * 用户-表情项关系管理
 */
public interface UserEmojiItemService {

    CurlResponse<List<UserEmojiItem>> listByUserId(Long userId);

    CurlResponse<String> save(UserEmojiItem relation);

    CurlResponse<String> update(UserEmojiItem relation);

    CurlResponse<String> delete(Long id);
}
