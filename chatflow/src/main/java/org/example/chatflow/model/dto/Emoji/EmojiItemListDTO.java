package org.example.chatflow.model.dto.Emoji;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "搜索表情包参数")
public class EmojiItemListDTO {
    @Schema(description = "名称")
    private String name;
}
