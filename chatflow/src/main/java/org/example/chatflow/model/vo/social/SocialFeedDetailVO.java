package org.example.chatflow.model.vo.social;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.entity.SocialFeed;
import org.example.chatflow.model.vo.common.FileCommonVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Data
@Schema(description = "动态详情")
public class SocialFeedDetailVO {

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

    @Schema(description = "发布者ID")
    private Long userId;

    @Schema(description = "是否已点赞")
    private Boolean isLike;

    @Schema(description = "文件列表")
    private List<FileCommonVO> files;
    private List<SocialFeedCommentItemVO> comments;

    @Mapper
    public interface SocialFeedDetailVOMapper {
        SocialFeedDetailVOMapper INSTANCE = Mappers.getMapper(SocialFeedDetailVOMapper.class);

        SocialFeedDetailVO toVO(SocialFeed feed);
    }
}
