package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.Emoji.EmojiPackUploadDTO;
import org.example.chatflow.model.vo.Emoji.EmojiPackListVO;

public interface EmojiService {

    CurlResponse<EmojiPackListVO> emojiPackList();
}
