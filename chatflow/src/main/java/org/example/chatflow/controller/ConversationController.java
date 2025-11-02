package org.example.chatflow.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.model.vo.SessionVO;
import org.example.chatflow.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author by zzr
 */
@RestController
@RequestMapping("/session")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConversationController {

    private final ConversationService conversationService;

    @Operation(description = "会话列表")
    @GetMapping("/sessionList")
    public CurlResponse<List<SessionVO>> getSessionList() {
        return conversationService.getSessionList();
    }

    @Operation(description = "设置常用会话")
    @PostMapping("/{conversationId}/common")
    public CurlResponse<String> setConversationCommon(@PathVariable Long conversationId,
                                                      @RequestParam(defaultValue = "true") boolean enable) {
        return conversationService.setConversationCommon(conversationId, enable);
    }

    @Operation(description = "删除会话")
    @DeleteMapping("/{conversationId}")
    public CurlResponse<String> deleteConversation(@PathVariable Long conversationId) {
        return conversationService.deleteConversation(conversationId);
    }
}
