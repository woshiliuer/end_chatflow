package org.example.chatflow.model.vo.Emoji;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "表情包列表(分页)")
public class EmojiPackListTotalVO {

    @Schema(description = "表情包列表")
    private List<EmojiPackListVO> emojiPackList;

    @Schema(description = "总数")
    private int total;
}
