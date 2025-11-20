package org.example.chatflow.model.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.chatflow.model.entity.ChatGroup;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * 编辑群聊参数
 */
@Data
@Schema(description = "编辑群聊参数")
public class EditGroupDTO {

    @Schema(description = "群聊ID")
    @NotNull(message = "群聊ID不能为空")
    private Long groupId;

    @Schema(description = "群聊名称，64字内")
    @NotBlank(message = "群聊名称不能为空")
    @Size(max = 64, message = "群聊名称长度不能超过64字")
    private String groupName;

    @Schema(description = "群聊简介，100字内")
    @Size(max = 100, message = "群聊简介长度不能超过100字")
    private String introduction;

    @Schema(description = "群公告，500字内")
    @Size(max = 500, message = "群公告长度不能超过500字")
    private String announcement;

    @Mapper
    public interface EditGroupDTOMapper {
        EditGroupDTOMapper INSTANCE = Mappers.getMapper(EditGroupDTOMapper.class);

        void update(@MappingTarget ChatGroup chatGroup, EditGroupDTO dto);
    }
}
