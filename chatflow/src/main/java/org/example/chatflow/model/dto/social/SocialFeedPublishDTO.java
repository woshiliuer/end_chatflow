package org.example.chatflow.model.dto.social;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.chatflow.model.dto.common.FileCommonDTO;

import java.util.List;

@Data
@Schema(description = "发布动态DTO")
public class SocialFeedPublishDTO {

    @Schema(description = "动态内容")
    @NotBlank(message = "动态内容必传")
    private String content;

    @Schema(description = "动态文件列表")
    private List<FileCommonDTO> files;
}
