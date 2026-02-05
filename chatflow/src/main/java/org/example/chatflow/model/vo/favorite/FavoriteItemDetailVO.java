package org.example.chatflow.model.vo.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.common.constants.OssConstant;
import org.example.chatflow.model.vo.common.FileCommonVO;

@Data
public class FavoriteItemDetailVO {

    @Schema(description = "收藏ID")
    private Long id;

    @Schema(description = "消息内容：文本为原文；表情/图片为对应内容")
    private String content;

    @Schema(description = "收藏类型：1单聊 2群聊 3其他")
    private Integer sourceType;

    @Schema(description = "收藏项类型：1文本 2表情/图片")
    private Integer itemType;

    @Schema(description = "发送人ID")
    private Long senderId;

    @Schema(description = "发送人名称")
    private String senderName;

    @Schema(description = "发送人头像")
    private FileCommonVO senderAvatar;

    @Schema(description = "发送时间")
    private Long sendTime;

    @Schema(description = "群聊ID")
    private Long groupId;

    @Schema(description = "群聊名称")
    private String groupName;

    @Schema(description = "群聊头像")
    private FileCommonVO groupAvatar;

    @Schema(description = "文件详情（图片/表情时使用）")
    private FileCommonVO fileDetail;
}
