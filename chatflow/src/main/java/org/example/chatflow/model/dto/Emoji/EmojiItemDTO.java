package org.example.chatflow.model.dto.Emoji;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.chatflow.model.entity.EmojiItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "表情项")
public class EmojiItemDTO {
    @Schema(description = "表情名称")
    private String name;

    @Schema(description = "表情类型：1unicode 2静态图 3动图")
    private Integer type;

    @Schema(description = "文件", required = true)
    @NotNull(message = "文件不能为空")
    private MultipartFile file;

    @Mapper
    public interface EmojiItemDTOMapper {
        EmojiItemDTOMapper INSTANCE = Mappers.getMapper(EmojiItemDTOMapper.class);

        EmojiItem toEntity(EmojiItemDTO dto);
    }
}