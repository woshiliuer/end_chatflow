package org.example.chatflow.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 群成员信息
 */
@Data
@Schema(description = "群成员信息")
public class GroupMemberVO {

    @Schema(description = "成员ID")
    private Long memberId;

    @Schema(description = "成员头像完整 URL")
    private String avatarFullUrl;
}

