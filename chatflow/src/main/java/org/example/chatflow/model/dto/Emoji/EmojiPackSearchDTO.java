package org.example.chatflow.model.dto.Emoji;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.dto.common.PageQueryDTO;

@Data
@Schema(description = "搜索官方表情包参数(分页)")
public class EmojiPackSearchDTO extends PageQueryDTO {

    @Schema(description = "名称(模糊匹配)，为空则查全部")
    private String name;
}
