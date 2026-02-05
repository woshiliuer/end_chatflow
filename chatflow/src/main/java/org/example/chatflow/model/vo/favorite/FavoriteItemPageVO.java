package org.example.chatflow.model.vo.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FavoriteItemPageVO {

    @Schema(description = "收藏ID")
    private Long id;

    @Schema(description = "消息内容：文本为原文；表情固定为[动画表情]")
    private String content;

    @Schema(description = "收藏类型：1单聊 2群聊 3其他")
    private Integer sourceType;

    @Schema(description = "发送人名称")
    private String senderName;

    @Schema(description = "发送时间")
    private Long sendTime;

    @Schema(description = "群聊名称")
    private String groupName;
}
