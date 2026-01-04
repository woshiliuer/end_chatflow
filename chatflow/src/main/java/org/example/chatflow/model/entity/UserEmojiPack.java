package org.example.chatflow.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.chatflow.common.entity.BaseEntity;

/**
 * 用户-表情包关系表
 */
@TableName(value = "user_emoji_pack")
@Data
public class UserEmojiPack extends BaseEntity<Long> {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 表情包ID
     */
    @TableField("pack_id")
    private Long packId;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 更新人ID
     */
    @TableField(value = "update_user_id", fill = FieldFill.UPDATE)
    private Long updateUserId;

    /**
     * 更新人名称
     */
    @TableField(value = "update_by", fill = FieldFill.UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private Long updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
