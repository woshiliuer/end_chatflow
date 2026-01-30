package org.example.chatflow.model.dto.Emoji;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.chatflow.model.dto.common.FileCommonDTO;

@Data
@Schema(description = "从消息图片添加为自定义表情")
public class AddEmojiFromMessageFileDTO {

    @Schema(description = "表情名称，不传则默认使用文件名")
    private String name;

    @Schema(description = "文件")
    @NotNull(message = "文件必传")
    private FileCommonDTO file;
}
