package org.example.chatflow.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import org.example.chatflow.model.entity.ChatGroup;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 群聊详情信息
 */
@Data
@Schema(description = "群聊详情信息")
public class GroupDetailVO {

    @Schema(description = "群聊ID")
    private Long id;

    @Schema(description = "群聊名称")
    private String groupName;

    @Schema(description = "群聊公告")
    private String announcement;

    @Schema(description = "群成员列表")
    private List<GroupMemberVO> members;

    @Mapper
    public interface GroupDetailVOMapper{
        GroupDetailVO.GroupDetailVOMapper INSTANCE = Mappers.getMapper(GroupDetailVO.GroupDetailVOMapper.class);

        GroupDetailVO toVO(ChatGroup group);
    }
}

