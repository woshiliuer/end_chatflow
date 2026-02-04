package org.example.chatflow.model.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class FavoriteItemPageQueryDTO {

    @Schema(description = "页码(从1开始)")
    @Min(1)
    private Integer page = 1;

    @Schema(description = "每页大小")
    @Min(1)
    private Integer size = 20;

    @Schema(description = "收藏类型：1文本 2表情")
    private Integer itemType;
}
