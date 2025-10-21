package org.example.chatflow.model.dto.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.stereotype.Service;

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
    private String email;

    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;
}
