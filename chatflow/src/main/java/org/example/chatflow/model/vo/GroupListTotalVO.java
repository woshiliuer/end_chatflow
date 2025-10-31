package org.example.chatflow.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author by zzr
 */
@Data
@Schema(description = "群聊列表汇总")
public class GroupListTotalVO {
    @Schema(description = "群聊列表")
    private List<GroupListVO> groupList;

    @Schema(description = "群聊列表总数")
    private int total;
}
