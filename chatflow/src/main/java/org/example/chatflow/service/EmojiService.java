package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.Emoji.EmojiPackUploadDTO;

public interface EmojiService {
    CurlResponse<Void> uploadEmojiPack(EmojiPackUploadDTO emojiPackUploadDTO);
}
