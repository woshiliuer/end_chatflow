package org.example.chatflow.model.vo.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.chatflow.model.entity.FavoriteItem;

@Data
public class FavoriteItemVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "所属用户ID")
    private Long userId;

    @Schema(description = "收藏类型：1文本 2表情")
    private Integer itemType;

    @Schema(description = "文本内容")
    private String textContent;

    @Schema(description = "来源类型：1单聊 2群聊 3其他")
    private Integer sourceType;

    @Schema(description = "发送人ID")
    private Long senderId;

    @Schema(description = "发送人名称")
    private String senderName;

    @Schema(description = "发送时间")
    private Long sendTime;

    @Schema(description = "群聊ID")
    private Long groupId;

    @Schema(description = "群聊名称")
    private String groupName;

    @Schema(description = "创建时间")
    private Long createTime;

    public static FavoriteItemVO fromEntity(FavoriteItem entity) {
        if (entity == null) {
            return null;
        }
        FavoriteItemVO vo = new FavoriteItemVO();
        vo.setId(entity.getId());
        vo.setUserId(entity.getUserId());
        vo.setItemType(entity.getItemType());
        vo.setTextContent(entity.getTextContent());
        vo.setSourceType(entity.getSourceType());
        vo.setSenderId(entity.getSenderId());
        vo.setSenderName(entity.getSenderName());
        vo.setSendTime(entity.getSendTime());
        vo.setGroupId(entity.getGroupId());
        vo.setGroupName(entity.getGroupName());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
