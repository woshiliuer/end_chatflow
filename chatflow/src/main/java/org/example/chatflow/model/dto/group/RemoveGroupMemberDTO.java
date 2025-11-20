package org.example.chatflow.model.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 移除群成员参数
 */
@Data
@Schema(description = "移除群成员参数")
public class RemoveGroupMemberDTO {

    @Schema(description = "群聊ID")
    @NotNull(message = "群聊ID不能为空")
    private Long groupId;

    @Schema(description = "要移除的成员ID列表")
    @NotEmpty(message = "成员ID列表不能为空")
    private List<Long> memberIds;
}
