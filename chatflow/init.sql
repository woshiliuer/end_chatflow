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

ALTER TABLE `user`
    ADD COLUMN `signature` VARCHAR(255) DEFAULT NULL COMMENT '个性签名' AFTER `password`,
ADD COLUMN `gender` TINYINT(1) DEFAULT NULL COMMENT '性别（0：未知，1：男，2：女）' AFTER `signature`;


CREATE TABLE friend_relation (
     `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
     `user_id`         BIGINT(20) NOT NULL COMMENT '用户ID',
     `friend_id`       BIGINT(20) NOT NULL COMMENT '好友ID',
     `remark`          VARCHAR(32) DEFAULT NULL COMMENT '备注/昵称，可选',
     `create_user_id` BIGINT(20) NOT NULL COMMENT '创建人ID',
     `create_by` VARCHAR(32) NOT NULL COMMENT '创建人名称',
     `create_time` BIGINT(20) NOT NULL COMMENT '创建时间',
     `update_user_id` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
     `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人名称',
     `update_time` BIGINT(20) DEFAULT NULL COMMENT '更新时间',
     PRIMARY KEY (id),
     UNIQUE KEY uk_user_friend (user_id, friend_id),
     KEY idx_friend_user (friend_id, user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '好友关系表';


CREATE TABLE friend_request (
    `id`             BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `requester_id`     BIGINT(20) NOT NULL COMMENT '申请方用户ID',
    `receiver_id`      BIGINT(20) NOT NULL COMMENT '接收方用户ID',
    `apply_message`    VARCHAR(255) DEFAULT NULL COMMENT '申请附言',
    `request_status`   TINYINT NOT NULL DEFAULT 0 COMMENT '申请状态：0待处理 1已同意 2已拒绝',
    `handled_at`       DATETIME DEFAULT NULL COMMENT '处理时间',
    `create_user_id` BIGINT(20) NOT NULL COMMENT '创建人ID',
    `create_by` VARCHAR(32) NOT NULL COMMENT '创建人名称',
    `create_time` BIGINT(20) NOT NULL COMMENT '创建时间',
    `update_user_id` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
    `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人名称',
    `update_time` BIGINT(20) DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '好友申请表';

CREATE TABLE conversation (
      `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
      `conversation_type` TINYINT NOT NULL COMMENT '会话类型：0单聊 1群聊',
      `last_message_id` BIGINT(20) DEFAULT NULL COMMENT '最新消息ID',
      `last_message_time` BIGINT(20) DEFAULT NULL COMMENT '最新消息时间（时间戳）',
      `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
      `create_user_id` BIGINT(20) NOT NULL COMMENT '创建人ID',
      `create_by` VARCHAR(32) NOT NULL COMMENT '创建人名称',
      `create_time` BIGINT(20) NOT NULL COMMENT '创建时间',
      `update_user_id` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
      `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人名称',
      `update_time` BIGINT(20) DEFAULT NULL COMMENT '更新时间',
      PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '会话表';


CREATE TABLE conversation_user (
      `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
      `conversation_id` BIGINT(20) NOT NULL COMMENT '会话ID',
      `user_id` BIGINT(20) NOT NULL COMMENT '参与用户ID',
      `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色：1普通成员 2管理员 3群主',
      `join_time` BIGINT(20) NOT NULL COMMENT '加入时间',
      `create_user_id` BIGINT(20) NOT NULL COMMENT '创建人ID',
      `create_by` VARCHAR(32) NOT NULL COMMENT '创建人名称',
      `create_time` BIGINT(20) NOT NULL COMMENT '创建时间',
      `update_user_id` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
      `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人名称',
      `update_time` BIGINT(20) DEFAULT NULL COMMENT '更新时间',
      PRIMARY KEY (id),
      UNIQUE KEY uk_conversation_user (conversation_id, user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '会话用户关系表';

CREATE TABLE message (
     `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
     `conversation_id` BIGINT(20) NOT NULL COMMENT '所属会话ID',
     `sender_id` BIGINT(20) NOT NULL COMMENT '发送方用户ID',
     `message_type` TINYINT NOT NULL DEFAULT 0 COMMENT '消息类型：1文本 2图片 3语音',
     `content` TEXT COMMENT '消息内容（JSON或纯文本）',
     `sequence` BIGINT(20) NOT NULL COMMENT '序号',
     `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 2撤回',
     `send_time` BIGINT(20) NOT NULL COMMENT '发送时间',
     `create_user_id` BIGINT(20) NOT NULL COMMENT '创建人ID',
     `create_by` VARCHAR(32) NOT NULL COMMENT '创建人名称',
     `create_time` BIGINT(20) NOT NULL COMMENT '创建时间',
     `update_user_id` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
     `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人名称',
     `update_time` BIGINT(20) DEFAULT NULL COMMENT '更新时间',
     PRIMARY KEY (id),
     UNIQUE KEY uk_conversation_sequence (conversation_id, sequence)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '消息表';

ALTER TABLE friend_request
    MODIFY COLUMN `handled_at` BIGINT(20) DEFAULT NULL COMMENT '处理时间';


ALTER TABLE `friend_request`
    ADD COLUMN `apply_remark` VARCHAR(500) DEFAULT NULL COMMENT '申请备注' AFTER `apply_message`;

