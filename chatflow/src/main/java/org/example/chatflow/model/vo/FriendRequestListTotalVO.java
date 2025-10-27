package org.example.chatflow.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author by zzr
 */
@Schema(description = "好友申请列表响应")
@Data
public class FriendRequestListTotalVO {
    @Schema(description = "好友申请列表")
    List<FriendRequestListVO> friendRequestList;

    @Schema(description = "总数")
    int total;

    @Schema(description = "待处理个数")
    int pendingCount;
}
