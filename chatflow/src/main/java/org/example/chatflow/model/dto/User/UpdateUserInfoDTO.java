package org.example.chatflow.model.dto.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author by zzr
 */
@Data
@Schema(description = "编辑个人资料")
public class UpdateUserInfoDTO {
    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "个性签名")
    private String signature;
}
