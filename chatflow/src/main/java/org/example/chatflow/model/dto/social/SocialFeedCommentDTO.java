package org.example.chatflow.model.dto.social;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "动态评论DTO")
public class SocialFeedCommentDTO {

    @NotNull(message = "动态ID不能为空")
    @Schema(description = "动态ID")
    private Long feedId;

    @NotBlank(message = "评论内容不能为空")
    @Schema(description = "评论内容")
    private String content;
}
