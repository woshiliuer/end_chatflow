package org.example.chatflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.entity.Param;
import org.example.chatflow.model.dto.MarkAsReadDTO;
import org.example.chatflow.model.dto.SendMessageDTO;
import org.example.chatflow.model.vo.MessageVO;
import org.example.chatflow.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author by zzr
 */
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "消息列表",description = "参数传会话id")
    @PostMapping("/messageList")
    public CurlResponse<List<MessageVO>> messageList(@RequestBody @Validated Param<Long> param){
        return messageService.messageList(param.getParam());
    }

    @Operation(summary = "发送消息", description = "发送消息并通过WebSocket推送")
    @PostMapping("/send")
    public CurlResponse<MessageVO> sendMessage(@RequestBody @Validated SendMessageDTO dto){
        return messageService.sendMessage(dto);
    }

    @Operation(summary = "标记已读", description = "将指定会话的所有消息标记为已读")
    @PostMapping("/markAsRead")
    public CurlResponse<Void> markAsRead(@RequestBody @Validated MarkAsReadDTO dto){
        return messageService.markAsRead(dto.getConversationId());
    }

}
