package org.example.chatflow.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket消息推送DTO
 * @author by zzr
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "WebSocket消息推送")
public class MessagePushDTO {
    
    @Schema(description = "消息ID")
    private Long id;
    
    @Schema(description = "消息ID（兼容旧字段）")
    private Long messageId;
    
    @Schema(description = "会话ID")
    private Long conversationId;
    
    @Schema(description = "发送者ID")
    private Long from;
    
    @Schema(description = "发送者ID（兼容旧字段）")
    private Long senderId;
    
    @Schema(description = "发送者昵称")
    private String senderNickname;

    @Schema(description = "发送者头像Url")
    private String avatarFullUrl;
    
    @Schema(description = "消息类型：1文本 2图片 3语音")
    private Integer messageType;
    
    @Schema(description = "消息内容")
    private String content;
    
    @Schema(description = "序号")
    private Long sequence;
    
    @Schema(description = "发送时间")
    private Long sendTime;
    
    @Schema(description = "状态：1正常 2撤回")
    private Integer status;
}

