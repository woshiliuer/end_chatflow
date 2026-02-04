package org.example.chatflow.model.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteItemCreateDTO {

    @Schema(description = "收藏类型：1文本 2表情")
    @NotNull
    private Integer itemType;

    @Schema(description = "文本内容")
    private String textContent;

    @Schema(description = "来源类型：1单聊 2群聊 3其他")
    @NotNull
    private Integer sourceType;

    @Schema(description = "发送人ID")
    private Long senderId;

    @Schema(description = "发送人名称")
    private String senderName;

    @Schema(description = "发送时间")
    private Long sendTime;

    @Schema(description = "群聊ID（source_type=2时使用）")
    private Long groupId;

    @Schema(description = "群聊名称（source_type=2时使用）")
    private String groupName;
}
