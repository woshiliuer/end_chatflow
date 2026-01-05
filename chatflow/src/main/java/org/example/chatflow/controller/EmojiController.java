package org.example.chatflow.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.Emoji.EmojiPackUploadDTO;
import org.example.chatflow.model.entity.EmojiItem;
import org.example.chatflow.model.vo.Emoji.EmojiPackListVO;
import org.example.chatflow.model.vo.common.FileCommonVO;
import org.example.chatflow.service.ConversationService;
import org.example.chatflow.service.EmojiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author by zzr
 */
@RestController
@RequestMapping("/emoji")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Tag(name = "表情包管理", description = "表情包相关的接口")
public class EmojiController {
    private final EmojiService emojiService;

    @Operation(summary = "表情包列表")
    @GetMapping("/emojiPackList")
    public CurlResponse<EmojiPackListVO> emojiPackList(){
        return emojiService.emojiPackList();
    }
}
