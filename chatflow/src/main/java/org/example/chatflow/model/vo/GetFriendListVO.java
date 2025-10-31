package org.example.chatflow.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.dto.User.RegisterDTO;
import org.example.chatflow.model.entity.FriendRelation;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author by zzr
 */
@Data
@Schema(description = "好友列表响应")
public class GetFriendListVO {
    @Schema(description = "好友id")
    private Long id;

    @Schema(description = "好友备注")
    private String remark;

    @Schema(description = "头像URL")
    private String avatarFullUrl;

    @Schema(description = "在线状态")
    private Integer status;

    @Mapper
    public interface FriendMapper {
        GetFriendListVO.FriendMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(GetFriendListVO.FriendMapper.class);

    }
}
