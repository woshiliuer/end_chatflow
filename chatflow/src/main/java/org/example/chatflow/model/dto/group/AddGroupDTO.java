package org.example.chatflow.model.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
import org.example.chatflow.model.entity.ChatGroup;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author by zzr
 */
@Data
@Schema(description = "新建群聊")
public class AddGroupDTO {
    @Schema(description = "群名")
    @NotBlank(message = "群聊不能为空")
    private String groupName;


    @Schema(description = "成员Id列表")
    @NotEmpty(message = "成员列表不能为空")
    List<Long> memberIds;

    @Mapper
    public interface AddGroupDTOMapper{
        AddGroupDTOMapper INSTANCE = Mappers.getMapper(AddGroupDTOMapper.class);
        ChatGroup toChatGroup(AddGroupDTO dto);
    }
}
