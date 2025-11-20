package org.example.chatflow.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author by zzr
 */
@Data
@Schema(description = "新增会话参数")
public class SaveSessionDTO {

    @Schema(description = "好友ID")
    @NotNull(message = "好友ID不能为空")
    private Long friendId;
}
