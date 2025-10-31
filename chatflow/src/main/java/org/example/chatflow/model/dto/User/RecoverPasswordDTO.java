package org.example.chatflow.model.dto.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author by zzr
 */
@Data
@Schema(description = "找回密码")
public class RecoverPasswordDTO {
    @Schema(description = "验证码")
    @NotBlank(message = "验证码不能为空")
    private String verfCode;

    @Schema(description = "新密码")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

    @Schema(description = "确认密码")
    @NotBlank(message = "确认密码不能为空")
    private String newPasswordConfirm;
}
