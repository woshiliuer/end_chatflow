package org.example.chatflow.model.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

/**
 * 邀请成员入群参数
 */
@Data
@Schema(description = "邀请成员入群参数")
public class InviteGroupMemberDTO {

    @Schema(description = "群聊ID")
    @NotNull(message = "群聊ID不能为空")
    private Long groupId;

    @Schema(description = "要邀请的成员ID列表")
    @NotEmpty(message = "成员ID列表不能为空")
    private List<Long> memberIds;
}
