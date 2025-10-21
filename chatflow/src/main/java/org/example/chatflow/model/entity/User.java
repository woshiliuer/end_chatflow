package org.example.chatflow.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.chatflow.common.entity.BaseEntity;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User extends BaseEntity<Long> {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 创建人ID
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Long createTime;

    /**
     * 更新人ID
     */
    @TableField(value = "update_user_id")
    private Long updateUserId;

    /**
     * 更新人名称
     */
    @TableField(value = "update_by")
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Long updateTime;

    /**
     * 是否删除（0：未删除，1：已删除）
     */
    @TableField(value = "deleted")
    private Integer deleted;
}