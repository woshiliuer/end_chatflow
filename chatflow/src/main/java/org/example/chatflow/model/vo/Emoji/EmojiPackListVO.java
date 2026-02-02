package org.example.chatflow.model.vo.Emoji;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.entity.EmojiPack;
import org.example.chatflow.model.vo.common.FileCommonVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Data
@Schema(description = "表情包列表返回")
public class EmojiPackListVO {
    @Schema(description = "表情包Id")
    private Long id;

    @Schema(description = "表情包名称")
    private String name;

    @Schema(description = "表情包类型")
    private Integer type;

    @Schema(description = "序号")
    private Integer sort;

    @Schema(description = "封面文件")
    private FileCommonVO cover;

    @Schema(description = "当前用户是否已添加")
    private Boolean bound;

    @Mapper
    public interface EmojiPackListVOMapper{
        EmojiPackListVOMapper  INSTANCE = Mappers.getMapper(EmojiPackListVOMapper.class);

        EmojiPackListVO toVO(EmojiPack entity);
    }
}
