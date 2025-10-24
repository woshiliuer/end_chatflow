package org.example.chatflow.model.dto.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.chatflow.model.dto.User.RegisterDTO;
import org.example.chatflow.model.entity.FriendRequest;
import org.mapstruct.Mapper;

/**
 * @author by zzr
 */
@Schema(description = "添加好友申请")
@Data
public class AddRequestDTO {

    @Schema(description = "好友的用户id")
    @NotNull
    private Long receiverId;

    @Schema(description = "申请附言")
    private String applyMessage;

    @Schema(description = "申请备注")
    private String remark;

    @Mapper
    public interface AddRequestDTOMapper{
        AddRequestDTO.AddRequestDTOMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(AddRequestDTO.AddRequestDTOMapper.class);

        FriendRequest toRequest(AddRequestDTO dto);
    }
}
