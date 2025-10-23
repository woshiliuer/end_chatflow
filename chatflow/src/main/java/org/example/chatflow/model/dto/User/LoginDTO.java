package org.example.chatflow.model.dto.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * @author by zzr
 */
@Data
@Schema(description = "登录请求参数")
public class LoginDTO {
    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    /**
     * 密码
     */
    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
}
