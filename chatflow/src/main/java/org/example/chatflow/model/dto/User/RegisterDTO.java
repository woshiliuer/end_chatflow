package org.example.chatflow.model.dto.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.chatflow.model.entity.User;
import org.mapstruct.Mapper;


/**
 * @author by zzr
 */
@Data
@Schema(description = "注册请求参数")
public class RegisterDTO {
    @Schema(description = "昵称")
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @Schema(description = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @Schema(description = "验证码")
    @NotBlank(message = "验证码不能为空")
    private String verificationCode;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Mapper
    public interface RegisterDTOMapper {
        RegisterDTOMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(RegisterDTOMapper.class);

        User toUser(RegisterDTO dto);
    }
}
