package org.example.chatflow.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.chatflow.common.entity.BaseEntity;

/**
 * 表情包表
 */
@TableName(value = "emoji_pack")
@Data
public class EmojiPack extends BaseEntity<Long> {

    /**
     * 表情包名称
     */
    @TableField("name")
    private String name;

    /**
     * 表情包类型：1-默认 2-自定义 3-官方
     */
    @TableField("type")
    private Integer type;

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
