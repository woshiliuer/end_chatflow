package org.example.chatflow.service;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.Emoji.AddEmojiFromMessageFileDTO;
import org.example.chatflow.model.dto.Emoji.CustomizeEmojiDTO;
import org.example.chatflow.model.vo.Emoji.CustomizeEmojisVO;
import org.example.chatflow.model.vo.Emoji.EmojiItemListVO;
import org.example.chatflow.model.vo.Emoji.EmojiPackListVO;

import java.util.List;

public interface EmojiService {

    CurlResponse<List<EmojiPackListVO>> myEmojiPackList();

    CurlResponse<List<EmojiItemListVO>> emojiItems(Long param);

    CurlResponse<EmojiPackListVO> emojiPackList();

    CurlResponse<Void> unbindEmojiPack(Long param);

    CurlResponse<Void> bindEmojiPack(Long param);

    CurlResponse<Void> unbindEmojiItem(Long param);

    CurlResponse<Void> addCustomizeEmoji(CustomizeEmojiDTO dto);

    CurlResponse<Void> addEmojiFromMessageFile(AddEmojiFromMessageFileDTO dto);

    CurlResponse<List<CustomizeEmojisVO>> customizeEmojis();
}
