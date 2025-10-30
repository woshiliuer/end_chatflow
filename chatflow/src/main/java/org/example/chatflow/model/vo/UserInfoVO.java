package org.example.chatflow.model.vo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.dto.User.RegisterDTO;
import org.example.chatflow.model.entity.User;
import org.mapstruct.Mapper;

/**
 * @author by zzr
 */
@Data
@Schema(description = "用户信息响应")
public class UserInfoVO {
    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "头像URL")
    private String avatarFullUrl;

    @Schema(description = "性别")
    private String genderDesc;

    @Schema(description = "个性签名")
    private String signature;

    @Mapper
    public interface UserInfoVOMapper{
        UserInfoVO.UserInfoVOMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(UserInfoVO.UserInfoVOMapper.class);

        UserInfoVO toVO(User user);
    }
}
