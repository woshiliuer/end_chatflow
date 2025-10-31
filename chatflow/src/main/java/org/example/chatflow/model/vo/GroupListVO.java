package org.example.chatflow.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.example.chatflow.common.constants.OssConstant;
import org.example.chatflow.model.entity.ChatGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author by zzr
 */
@Data
@Schema(description = "群聊列表")
public class GroupListVO {
    @Schema(description = "群聊id")
    private Long id;

    @Schema(description = "群聊名")
    private String groupName;

    @Schema(description = "群聊头像Url")
    private String groupAvatarFullUrl;

    @Schema(description = "所处角色")
    private Integer role;

    @Mapper
    public interface GroupListVOMapper {
        GroupListVOMapper INSTANCE = Mappers.getMapper(GroupListVOMapper.class);

        GroupListVO toVO(ChatGroup chatGroup);

    }
}
