package org.example.chatflow.model.dto.Emoji;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 上传表情包DTO
 * @author by zzr
 */
@Data
@Schema(description = "上传表情包")
public class EmojiPackUploadDTO {
    @NotBlank(message = "表情包名称不能为空")
    private String name;

    @NotBlank(message = "文件夹名称不能为空")
    private String folderName;

    @Schema(description = "封面图片")
    @NotNull(message = "封面图片不能为空")
    private MultipartFile coverImage;

    @NotEmpty(message = "表情项列表不能为空")
    @Schema(description = "表情项列表")
    private List<EmojiItemDTO> emojiItems;
}
