package org.example.chatflow.model.dto.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author by zzr
 */
@Data
@Schema(description = "获取验证码")
public class GetVerfCodeDTO {
    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "验证码类型 1：注册 2：找回密码")
    private Integer verfCodeType;
}
