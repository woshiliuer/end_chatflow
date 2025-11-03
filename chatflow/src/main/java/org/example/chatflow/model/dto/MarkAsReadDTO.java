package org.example.chatflow.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 标记消息已读请求DTO
 * @author by zzr
 */
@Data
@Schema(description = "标记消息已读请求")
public class MarkAsReadDTO {
    
    @NotNull(message = "会话ID不能为空")
    @Schema(description = "会话ID")
    private Long conversationId;
}

