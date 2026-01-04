package org.example.chatflow.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.dto.Emoji.EmojiPackUploadDTO;
import org.example.chatflow.service.ConversationService;
import org.example.chatflow.service.EmojiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author by zzr
 */
@RestController
@RequestMapping("/emoji")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmojiController {
    private final EmojiService emojiService;

    @Operation(summary = "官方上传表情包")
    @PostMapping("/uploadEmojiPack")
    public CurlResponse<Void> uploadEmojiPack(@RequestBody @Validated EmojiPackUploadDTO emojiPackUploadDTO) {
        return emojiService.uploadEmojiPack(emojiPackUploadDTO);
    }

}
