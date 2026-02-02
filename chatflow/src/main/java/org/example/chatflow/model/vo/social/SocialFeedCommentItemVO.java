package org.example.chatflow.model.vo.social;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.entity.SocialFeedComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Data
@Schema(description = "动态评论项")
public class SocialFeedCommentItemVO {

    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户头像")
    private String avatarFullUrl;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "评论时间")
    private Long createTime;

    @Mapper
    public interface SocialFeedCommentItemVOMapper {
        SocialFeedCommentItemVOMapper INSTANCE = Mappers.getMapper(SocialFeedCommentItemVOMapper.class);

        @Mapping(target = "id", source = "id")
        @Mapping(target = "userId", source = "userId")
        @Mapping(target = "createTime", source = "createTime")
        SocialFeedCommentItemVO toVO(SocialFeedComment comment);
    }
}
