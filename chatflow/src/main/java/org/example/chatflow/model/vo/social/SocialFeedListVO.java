package org.example.chatflow.model.vo.social;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.entity.SocialFeed;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Data
@Schema(description = "动态列表项")
public class SocialFeedListVO {

    @Schema(description = "动态ID")
    private Long id;

    @Schema(description = "用户头像")
    private String avatarFullUrl;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "动态内容")
    private String content;

    @Schema(description = "点赞个数")
    private Long likeCount;

    @Schema(description = "评论个数")
    private Long commentCount;

    @Mapper
    public interface SocialFeedListVOMapper {
        SocialFeedListVOMapper INSTANCE = Mappers.getMapper(SocialFeedListVOMapper.class);

        SocialFeedListVO toVO(SocialFeed feed);
    }
}
