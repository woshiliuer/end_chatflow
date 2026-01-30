package org.example.chatflow.model.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "分页查询参数")
public class PageQueryDTO {

    @Schema(description = "页码，从1开始")
    @NotNull(message = "page必填")
    private Integer page;

    @Schema(description = "每页大小")
    @NotNull(message = "size必填")
    private Integer size;
}
