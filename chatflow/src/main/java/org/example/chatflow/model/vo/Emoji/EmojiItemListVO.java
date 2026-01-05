package org.example.chatflow.model.vo.Emoji;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.vo.common.FileCommonVO;

@Data
@Schema(description = "表情包项列表")
public class EmojiItemListVO {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "表情类型：1Unicode 2静态图 3动图")
    private Integer type;

    @Schema(description = "文件")
    private FileCommonVO emojiItemFile;

    @Schema(description = "unicode")
    private String unicodeVal;
}
