package org.example.chatflow.model.vo;

/**
 * @author by zzr
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.entity.User;
import org.mapstruct.Mapper;

@Data
@Schema(description = "根据邮箱查询的用户信息响应")
public class UserByEmailVO {

    @Schema(description = "用户Id")
    private Long Id;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "头像URL")
    private String avatarFullUrl;

    @Mapper
    public interface UserByEmailVOMapper{
        UserByEmailVO.UserByEmailVOMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(UserByEmailVO.UserByEmailVOMapper.class);

        UserByEmailVO toVO(User user);
    }

}
