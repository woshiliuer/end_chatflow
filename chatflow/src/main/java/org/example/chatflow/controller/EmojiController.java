package org.example.chatflow.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.entity.Param;
import org.example.chatflow.model.dto.Emoji.CustomizeEmojiDTO;
import org.example.chatflow.model.dto.Emoji.EmojiItemListDTO;
import org.example.chatflow.model.vo.Emoji.CustomizeEmojisVO;
import org.example.chatflow.model.vo.Emoji.EmojiItemListVO;
import org.example.chatflow.model.vo.Emoji.EmojiPackListVO;
import org.example.chatflow.service.EmojiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "我的表情包列表")
    @GetMapping("/myEmojiPackList")
    public CurlResponse<List<EmojiPackListVO>> myEmojiPackList(){
        return emojiService.myEmojiPackList();
    }

    @Operation(summary = "表情包下的表情包项",description = "参数传表情包Id")
    @PostMapping("/emojiItemList")
    public CurlResponse<List<EmojiItemListVO>> emojiItems(@RequestBody @Validated Param<Long> param){
        return emojiService.emojiItems(param.getParam());
    }

    @Operation(summary = "搜索表情包")
    @PostMapping("/emojiPackList")
    public CurlResponse<EmojiPackListVO> emojiPackList(@RequestBody EmojiItemListDTO dto){
        return emojiService.emojiPackList();
    }

    @Operation(summary = "用户绑定表情包",description = "参数传表情包Id")
    @PostMapping("/bindEmojiPack")
    public CurlResponse<Void> bindEmojiPack(@RequestBody @Validated Param<Long> param){
        return emojiService.bindEmojiPack(param.getParam());
    }

    @Operation(summary = "用户解绑表情包",description = "参数传表情包Id")
    @PostMapping("/unbindEmojiPack")
    public CurlResponse<Void> unbindEmojiPack(@RequestBody @Validated Param<Long> param){
        return emojiService.unbindEmojiPack(param.getParam());
    }

    @Operation(summary = "用户绑定自定义表情",description = "参数传表情项Id")
    @PostMapping("/bindEmojiItem")
    public CurlResponse<Void> collectEmojiItem(@RequestBody @Validated Param<Long> param){
        return emojiService.collectEmojiItem(param.getParam());
    }

    @Operation(summary = "用户删除表情",description = "参数传表情项Id")
    @PostMapping("/unbindEmojiItem")
    public CurlResponse<Void> unbindEmojiItem(@RequestBody @Validated Param<Long> param){
        return emojiService.unbindEmojiItem(param.getParam());
    }

    @Operation(summary = "用户添加自定义表情")
    @PostMapping("/addCustomizeEmoji")
    public CurlResponse<Void> addCustomizeEmoji(@RequestBody @Validated CustomizeEmojiDTO dto){
        return emojiService.addCustomizeEmoji(dto);
    }

    @Operation(summary = "用户的自定义表情包")
    @GetMapping("/customizeEmojis")
    public CurlResponse<List<CustomizeEmojisVO>> customizeEmojis(){
        return emojiService.customizeEmojis();
    }

}
