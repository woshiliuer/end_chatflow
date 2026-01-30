package org.example.chatflow.model.vo.Emoji;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.vo.common.FileCommonVO;

@Data
@Schema(description = "用户的自定义表情包")
public class CustomizeEmojisVO {
    @Schema(description = "表情包项id")
    private Long id;

    @Schema(description = "表情包项名称")
    private String name;

    @Schema(description = "表情类型：1Unicode 2静态图 3动图 用户自定义的只能是静态图/动图")
    private Integer type;

    @Schema(description = "文件")
    private FileCommonVO file;

}
