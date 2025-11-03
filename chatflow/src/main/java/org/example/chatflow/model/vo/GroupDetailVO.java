package org.example.chatflow.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * 群聊详情信息
 */
@Data
@Schema(description = "群聊详情信息")
public class GroupDetailVO {

    @Schema(description = "群聊ID")
    private Long groupId;

    @Schema(description = "群聊名称")
    private String groupName;

    @Schema(description = "群聊公告")
    private String announcement;

    @Schema(description = "群成员列表")
    private List<GroupMemberVO> members;
}

