package org.example.chatflow.model.dto.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author by zzr
 */
@Schema(description = "同意好友申请")
@Data
public class AgreeRequestDTO {
    /**
     * 好友Id
     */
    private Long friendId;

    /**
     * 备注
     */
    private String remark;
}
