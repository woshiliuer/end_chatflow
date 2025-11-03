package org.example.chatflow.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



/**
 * 发送消息DTO
 * @author by zzr
 */
@Data
@Schema(description = "发送消息请求")
public class SendMessageDTO {
    
    @NotNull(message = "会话ID不能为空")
    @Schema(description = "会话ID")
    private Long conversationId;
    
    @NotNull(message = "消息类型不能为空")
    @Schema(description = "消息类型：1文本 2图片 3语音")
    private Integer messageType;
    
    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "消息内容")
    private String content;
}

