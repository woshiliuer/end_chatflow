package org.example.chatflow.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author by zzr
 */
@Schema(description = "好友详情")
@Data
public class FriendDetailVO {
    @Schema(description = "好友id")
    private Long id;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "好友昵称")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "好友头像Url")
    private String avatarFullUrl;

    @Schema(description = "个性签名")
    private String signature;

    @Mapper
    public interface FriendDetailVOMapper {
        FriendDetailVO.FriendDetailVOMapper INSTANCE = Mappers.getMapper(FriendDetailVO.FriendDetailVOMapper.class);

        FriendDetailVO toVO(User friend);
    }
}
