package org.example.chatflow.model.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteItemCollectFromMessageDTO {

    @Schema(description = "会话ID")
    @NotNull
    private Long conversationId;

    @Schema(description = "消息ID")
    @NotNull
    private Long messageId;
}
