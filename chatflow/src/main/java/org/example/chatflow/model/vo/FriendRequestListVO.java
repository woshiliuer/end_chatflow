package org.example.chatflow.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author by zzr
 */
@Data
public class FriendRequestListVO {
    @Schema(description = "用户Id")
    private Long userId;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "申请附言")
    private String applyMessage;

    @Schema(description = "申请时间")
    private Long createTime;

    @Schema(description = "头像Url")
    private String avatarFullUrl;

    @Schema(description = "申请方向：1=我发出的申请，2=收到的申请")
    private Integer applyDirection;

    @Schema(description = "申请状态：0待处理 1已同意 2已拒绝")
    private Integer requestStatus;
}
