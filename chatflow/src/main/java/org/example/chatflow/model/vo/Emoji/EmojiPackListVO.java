package org.example.chatflow.model.vo.Emoji;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.vo.common.FileCommonVO;

@Data
@Schema(description = "表情包列表返回")
public class EmojiPackListVO {
    @Schema(description = "表情包Id")
    private Long id;

    @Schema(description = "表情包名称")
    private String name;

    @Schema(description = "封面文件")
    private FileCommonVO cover;
}
