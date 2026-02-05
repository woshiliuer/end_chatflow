package org.example.chatflow.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.entity.User;
import org.mapstruct.Mapper;

/**
 * @author by zzr
 */
@Data
@Schema(description = "用户信息响应")
public class UserInfoVO {
    @Schema(description = "用户id")
    private Long id;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "消息通知是否开启（1：关闭，2：开启）")
    private Integer notificationEnabled;

    @Schema(description = "头像URL")
    private String avatarFullUrl;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "个性签名")
    private String signature;

    @Mapper
    public interface UserInfoVOMapper{
        UserInfoVO.UserInfoVOMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(UserInfoVO.UserInfoVOMapper.class);

        UserInfoVO toVO(User user);
    }
}
