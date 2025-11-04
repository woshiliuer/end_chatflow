package org.example.chatflow.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 群成员信息
 */
@Data
@Schema(description = "群成员信息")
public class GroupMemberVO {

    @Schema(description = "成员ID")
    private Long memberId;

    @Schema(description = "成员昵称")
    private String nickname;

    @Schema(description = "所处群聊角色")
    private Integer role;

    @Schema(description = "成员头像完整 URL")
    private String avatarFullUrl;

}

