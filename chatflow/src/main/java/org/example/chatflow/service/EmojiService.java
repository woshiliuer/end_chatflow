package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.Emoji.CustomizeEmojiDTO;
import org.example.chatflow.model.vo.Emoji.EmojiItemListVO;
import org.example.chatflow.model.vo.Emoji.EmojiPackListVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmojiService {

    CurlResponse<EmojiPackListVO> myEmojiPackList();

    CurlResponse<List<EmojiItemListVO>> emojiItems(Long param);

    CurlResponse<EmojiPackListVO> emojiPackList();

    CurlResponse<Void> unbindEmojiPack(Long param);

    CurlResponse<Void> collectEmojiItem(Long param);

    CurlResponse<Void> bindEmojiPack(Long param);

    CurlResponse<Void> unbindEmojiItem(Long param);

    CurlResponse<Void> customizeEmoji(CustomizeEmojiDTO dto);

    CurlResponse<Void> uploadEmoji(MultipartFile file);
}
