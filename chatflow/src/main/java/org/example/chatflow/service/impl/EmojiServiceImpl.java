package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.Emoji.CustomizeEmojiDTO;
import org.example.chatflow.model.vo.Emoji.EmojiItemListVO;
import org.example.chatflow.model.vo.Emoji.EmojiPackListVO;
import org.example.chatflow.service.EmojiItemService;
import org.example.chatflow.service.EmojiPackService;
import org.example.chatflow.service.EmojiService;
import org.example.chatflow.service.FileService;
import org.example.chatflow.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * @author by zzr
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmojiServiceImpl implements EmojiService {


    private final AliOssUtil aliOssUtil;

    private final EmojiPackService emojiPackService;

    private final FileService fileService;

    private final EmojiItemService emojiItemService;

    @Override
    public CurlResponse<Void> bindEmojiPack(Long param) {
        return null;
    }

    @Override
    public CurlResponse<EmojiPackListVO> myEmojiPackList() {
        return null;
    }

    @Override
    public CurlResponse<List<EmojiItemListVO>> emojiItems(Long param) {
        return null;
    }

    @Override
    public CurlResponse<EmojiPackListVO> emojiPackList() {
        return null;
    }

    @Override
    public CurlResponse<Void> unbindEmojiPack(Long param) {
        return null;
    }

    @Override
    public CurlResponse<Void> collectEmojiItem(Long param) {
        return null;
    }

    @Override
    public CurlResponse<Void> unbindEmojiItem(Long param) {
        return null;
    }

    @Override
    public CurlResponse<Void> customizeEmoji(CustomizeEmojiDTO dto) {
        return null;
    }

    @Override
    public CurlResponse<Void> uploadEmoji(MultipartFile file) {
        return null;
    }
}
