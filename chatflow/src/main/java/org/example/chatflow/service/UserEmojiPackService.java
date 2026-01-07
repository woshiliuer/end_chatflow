package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.entity.UserEmojiPack;

import java.util.List;

/**
 * 用户-表情包关系管理
 */
public interface UserEmojiPackService {

    List<UserEmojiPack> listByUserId(Long userId);

    CurlResponse<String> save(UserEmojiPack relation);

    CurlResponse<String> update(UserEmojiPack relation);

    CurlResponse<String> delete(Long id);
}
