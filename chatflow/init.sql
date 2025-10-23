CREATE TABLE `user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `email` VARCHAR(50) NOT NULL COMMENT '邮箱',
    `password` VARCHAR(128) NOT NULL COMMENT '密码',
    `create_user_id` BIGINT(20) NOT NULL COMMENT '创建人ID',
    `create_by` VARCHAR(32) NOT NULL COMMENT '创建人名称',
    `create_time` BIGINT(20) NOT NULL COMMENT '创建时间',
    `update_user_id` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
    `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人名称',
    `update_time` BIGINT(20) DEFAULT NULL COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

ALTER TABLE `user`
    ADD COLUMN `nickname` varchar(50) DEFAULT NULL COMMENT '昵称' AFTER `password`;

ALTER TABLE `user`
    ADD COLUMN `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL' AFTER `nickname`;
