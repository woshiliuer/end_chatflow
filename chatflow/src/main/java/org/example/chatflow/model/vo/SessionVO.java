package org.example.chatflow.model.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.entity.Conversation;
import org.example.chatflow.model.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author by zzr
 */
@Data
@Schema(description = "会话列表")
public class SessionVO {
    @Schema(description = "好友id/群聊id")
    private Long id;

    @Schema(description = "显示名称（好友备注或群聊名称）")
    private String displayName;

    @Schema(description = "好友/群聊头像")
    private String avatarFullUrl;

    @Schema(description = "最近的消息id")
    private Long lastMessageId;

    @Schema(description = "最近的消息内容")
    private String content;

    @Schema(description = "最近的消息发送时间")
    private Long sendTime;

    @Schema(description = "未读消息数")
    private int unreadCount;

    @Schema(description = "会话状态：1正常 2隐藏 3常用")
    private int status;

    @Mapper
    public interface SessionVOMapper{
        SessionVO.SessionVOMapper INSTANCE = Mappers.getMapper(SessionVOMapper.class);

        @Mapping(source = "conversation.id", target = "id")
        @Mapping(source = "message.id", target = "lastMessageId")
        @Mapping(source = "message.content", target = "content")
        @Mapping(source = "message.sendTime", target = "sendTime")
        @Mapping(source = "unreadCount", target = "unreadCount")
        @Mapping(source = "status", target = "status")
        SessionVO toVO(Conversation conversation, Message message, int unreadCount, int status);
    }
}
