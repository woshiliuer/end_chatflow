package org.example.chatflow.model.dto.Emoji;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.chatflow.model.dto.common.FileCommonDTO;

@Data
@Schema(description = "自定义表情包")
public class CustomizeEmojiDTO {
    @Schema(description = "表情包名称")
    @NotBlank(message = "表情包名称必填")
    private String name;

    @Schema(description = "表情类型：1unicode 2静态图 3动图 用户只能添加静态图/动图")
    @NotNull(message = "表情类型必填")
    @Min(2)
    @Max(3)
    private Integer type;

    @Schema(description = "文件")
    @NotNull(message = "文件必传")
    private FileCommonDTO file;
}
