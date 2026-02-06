package org.example.chatflow.model.dto.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "修改好友备注DTO")
public class UpdateRemarkDTO {

    @NotNull(message = "好友ID不能为空")
    @Schema(description = "好友ID")
    private Long friendId;

    @Schema(description = "新备注")
    private String remark;
}
