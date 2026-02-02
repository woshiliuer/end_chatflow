package org.example.chatflow.model.vo.social;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "动态列表(分页)")
public class SocialFeedListTotalVO {

    @Schema(description = "动态列表")
    private List<SocialFeedListVO> feedList;

    @Schema(description = "总数")
    private long total;
}
