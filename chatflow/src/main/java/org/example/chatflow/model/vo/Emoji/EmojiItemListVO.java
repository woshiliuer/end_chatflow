package org.example.chatflow.model.vo.Emoji;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.entity.EmojiItem;
import org.example.chatflow.model.vo.common.FileCommonVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Data
@Schema(description = "表情包项列表")
public class EmojiItemListVO {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "表情类型：1Unicode 2静态图 3动图")
    private Integer type;

    @Schema(description = "文件")
    private FileCommonVO emojiItemFile;

    @Schema(description = "unicode")
    private String unicodeVal;

    @Mapper
    public interface EmojiItemListVOMapper {
        EmojiItemListVOMapper INSTANCE = Mappers.getMapper(EmojiItemListVOMapper.class);

        EmojiItemListVO toVO(EmojiItem entity);
    }
}
