package org.example.chatflow.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.common.constants.FileSourceTypeConstant;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.Emoji.EmojiItemDTO;
import org.example.chatflow.model.dto.Emoji.EmojiPackUploadDTO;
import org.example.chatflow.model.dto.common.FileCommonDTO;
import org.example.chatflow.model.entity.EmojiItem;
import org.example.chatflow.model.entity.EmojiPack;
import org.example.chatflow.model.vo.Emoji.EmojiPackListVO;
import org.example.chatflow.service.EmojiItemService;
import org.example.chatflow.service.EmojiPackService;
import org.example.chatflow.service.EmojiService;
import org.example.chatflow.service.FileService;
import org.example.chatflow.utils.AliOssUtil;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
    public CurlResponse<EmojiPackListVO> emojiPackList() {
        return null;
    }
}
