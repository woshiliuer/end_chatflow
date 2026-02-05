package org.example.chatflow.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.chatflow.model.entity.Message;
import org.example.chatflow.model.vo.common.FileCommonVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author by zzr
 */
@Data
@Schema(description = "消息VO")
public class MessageVO {
    @Schema(description = "数据库ID")
    private Long id;

    @NotNull(message = "消息类型不能为空")
    @Schema(description = "消息类型：1文本 2文件 3语音")
    private Integer messageType;

    @Schema(description = "序号")
    private Long sequence;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "消息文件")
    private FileCommonVO messageFile;

    @Schema(description = "状态：1正常 2撤回")
    private Integer status;

    @Schema(description = "发送时间")
    private Long sendTime;

    @Schema(description = "表示对方发送给我的，还是我发送给对方的")
    private Integer direction;

    @Schema(description = "头像Url")
    private String avatarFullUrl;

    @Mapper
    public interface MessageVOMapper {
        MessageVO.MessageVOMapper INSTANCE = Mappers.getMapper(MessageVO.MessageVOMapper.class);
        MessageVO toVO(Message message);
    }
}
