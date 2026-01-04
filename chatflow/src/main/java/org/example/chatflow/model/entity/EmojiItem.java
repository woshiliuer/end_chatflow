package org.example.chatflow.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.chatflow.common.entity.BaseEntity;

/**
 * 表情项表
 */
@TableName(value = "emoji_item")
@Data
public class EmojiItem extends BaseEntity<Long> {

    /**
     * 所属表情包ID
     */
    @TableField("pack_id")
    private Long packId;

    /**
     * 表情项名称
     */
    @TableField("name")
    private String name;

    /**
     * 表情类型：1Unicode 2静态图 3动图
     */
    @TableField("type")
    private Integer type;

    /**
     * Unicode表情字符
     */
    @TableField("unicode_val")
    private String unicodeVal;

    /**
     * 静态图、动图资源URL
     */
    @TableField("url")
    private String url;

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
