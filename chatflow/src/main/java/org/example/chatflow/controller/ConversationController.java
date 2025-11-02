package org.example.chatflow.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.entity.Param;
import org.example.chatflow.model.vo.SessionVO;
import org.example.chatflow.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author by zzr
 */
@RestController
@RequestMapping("/session")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConversationController {

    private final ConversationService conversationService;

    @Operation(summary = "会话列表")
    @GetMapping("/sessionList")
    public CurlResponse<List<SessionVO>> getSessionList() {
        return conversationService.getSessionList();
    }

    @Operation(summary = "设置常用会话",description = "参数传会话Id")
    @PostMapping("/setFavorite")
    public CurlResponse<String> setFavorite(@RequestBody @Validated Param<Long> param) {
        return conversationService.setFavorite(param.getParam());
    }

    @Operation(summary = "取消常用会话",description = "参数传会话Id")
    @PostMapping("/cancelFavorite")
    public CurlResponse<String> cancelFavorite(@RequestBody @Validated Param<Long> param) {
        return conversationService.cancelFavorite(param.getParam());
    }

    @Operation(summary = "删除会话", description = "参数传会话Id")
    @PostMapping("/deleteSession")
    public CurlResponse<String> deleteSession(@RequestBody @Validated Param<Long> param) {
        return conversationService.deleteConversation(param.getParam());
    }

}
