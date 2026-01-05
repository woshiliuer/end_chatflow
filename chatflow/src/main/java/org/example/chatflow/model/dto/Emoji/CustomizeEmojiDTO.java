package org.example.chatflow.model.dto.Emoji;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.dto.common.FileCommonDTO;

@Data
@Schema(description = "自定义表情包")
public class CustomizeEmojiDTO {
    @Schema(description = "表情包名称")
    private String name;

    @Schema(description = "表情类型：1unicode 2静态图 3动图 用户只能添加静态图/动图")
    private Integer type;

    @Schema(description = "文件")
    private FileCommonDTO file;
}
