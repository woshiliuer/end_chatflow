package org.example.chatflow.model.dto.social;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.dto.common.PageQueryDTO;

@Data
@Schema(description = "动态列表查询参数(分页)")
public class SocialFeedListQueryDTO extends PageQueryDTO {

    @Schema(description = "动态内容(模糊搜索)，为空则查全部")
    private String content;
}
