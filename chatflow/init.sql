
CREATE TABLE `user` (
                        `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                        `email` VARCHAR(50) NOT NULL COMMENT '邮箱',
                        `password` VARCHAR(128) NOT NULL COMMENT '密码',
                        `signature` VARCHAR(255) DEFAULT NULL COMMENT '个性签名',
                        `gender` TINYINT(1) DEFAULT NULL COMMENT '性别（0：未知，1：男，2：女）',
                        `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
                        `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
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



CREATE TABLE `friend_relation` (
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
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_user_friend` (`user_id`, `friend_id`),
                                   KEY `idx_friend_user` (`friend_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友关系表';



CREATE TABLE `friend_request` (
                                  `id`              BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `requester_id`    BIGINT(20) NOT NULL COMMENT '申请方用户ID',
                                  `receiver_id`     BIGINT(20) NOT NULL COMMENT '接收方用户ID',
                                  `apply_message`   VARCHAR(255) DEFAULT NULL COMMENT '申请附言',
                                  `apply_remark`    VARCHAR(500) DEFAULT NULL COMMENT '申请备注',
                                  `request_status`  TINYINT NOT NULL DEFAULT 0 COMMENT '申请状态：0待处理 1已同意 2已拒绝',
                                  `handled_at`      BIGINT(20) DEFAULT NULL COMMENT '处理时间',
                                  `create_user_id`  BIGINT(20) NOT NULL COMMENT '创建人ID',
                                  `create_by`       VARCHAR(32) NOT NULL COMMENT '创建人名称',
                                  `create_time`     BIGINT(20) NOT NULL COMMENT '创建时间',
                                  `update_user_id`  BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
                                  `update_by`       VARCHAR(32) DEFAULT NULL COMMENT '更新人名称',
                                  `update_time`     BIGINT(20) DEFAULT NULL COMMENT '更新时间',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友申请表';



CREATE TABLE `conversation` (
                                `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                `conversation_type` TINYINT NOT NULL COMMENT '会话类型：0单聊 1群聊',
                                `group_id` BIGINT(20) DEFAULT NULL COMMENT '群聊ID（当会话类型为群聊时关联群聊表主键）',
                                `last_message_id` BIGINT(20) DEFAULT NULL COMMENT '最新消息ID',
                                `last_message_time` BIGINT(20) DEFAULT NULL COMMENT '最新消息时间（时间戳）',
                                `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
                                `create_user_id` BIGINT(20) NOT NULL COMMENT '创建人ID',
                                `create_by` VARCHAR(32) NOT NULL COMMENT '创建人名称',
                                `create_time` BIGINT(20) NOT NULL COMMENT '创建时间',
                                `update_user_id` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
                                `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人名称',
                                `update_time` BIGINT(20) DEFAULT NULL COMMENT '更新时间',
                                PRIMARY KEY (`id`),
                                KEY `idx_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话表';




CREATE TABLE `conversation_user` (
                                     `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `conversation_id` BIGINT(20) NOT NULL COMMENT '会话ID',
                                     `user_id` BIGINT(20) NOT NULL COMMENT '参与用户ID',
                                     `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色：1普通成员 2管理员 3群主',
                              `join_time` BIGINT(20) NOT NULL COMMENT '加入时间',
                              `last_read_seq` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '最后已读消息序号',
                              `last_read_time` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '最后已读时间（毫秒时间戳）',
                              `status` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '会话状态：1正常 2隐藏 3常用',
                              `create_user_id` BIGINT(20) NOT NULL COMMENT '创建人ID',
                              `create_by` VARCHAR(32) NOT NULL COMMENT '创建人名称',
                              `create_time` BIGINT(20) NOT NULL COMMENT '创建时间',
                              `update_user_id` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
                              `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人名称',
                              `update_time` BIGINT(20) DEFAULT NULL COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_conversation_user` (`conversation_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话用户关系表';



CREATE TABLE `chat_group_user` (
                                   `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `group_id` BIGINT(20) NOT NULL COMMENT '群聊ID',
                                   `member_id` BIGINT(20) NOT NULL COMMENT '成员用户ID',
                                   `role` TINYINT NOT NULL DEFAULT 1 COMMENT '角色：1成员 2管理员 3群主',
                                   `join_time` BIGINT(20) NOT NULL COMMENT '加入时间（秒时间戳）',
                                   `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 2已退出/移除',
                                   `create_user_id` BIGINT(20) NOT NULL COMMENT '创建人ID',
                                   `create_by` VARCHAR(32) NOT NULL COMMENT '创建人名称',
                                   `create_time` BIGINT(20) NOT NULL COMMENT '创建时间',
                                   `update_user_id` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
                                   `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人名称',
                                   `update_time` BIGINT(20) DEFAULT NULL COMMENT '更新时间',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_group_member` (`group_id`, `member_id`),
                                   KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群聊成员关系表';



CREATE TABLE `message` (
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
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uk_conversation_sequence` (`conversation_id`, `sequence`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

CREATE TABLE `chat_group` (
                              `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                              `group_name` VARCHAR(64) NOT NULL COMMENT '群名称',
                              `group_avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '群头像URL',
                              `announcement` TEXT COMMENT '群公告',
                              `owner_id` BIGINT(20) NOT NULL COMMENT '群主用户ID',
                              `create_user_id` BIGINT(20) NOT NULL COMMENT '创建人ID',
                              `create_by` VARCHAR(32) NOT NULL COMMENT '创建人名称',
                              `create_time` BIGINT(20) NOT NULL COMMENT '创建时间',
                              `update_user_id` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
                              `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人名称',
                              `update_time` BIGINT(20) DEFAULT NULL COMMENT '更新时间',
                              `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0未删除 1已删除）',
                              PRIMARY KEY (`id`),
                              KEY `idx_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群聊表';

ALTER TABLE `chat_group`
    MODIFY COLUMN `deleted` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否删除（1未删除 2已删除）';

ALTER TABLE `conversation`
    MODIFY COLUMN `conversation_type` TINYINT NOT NULL COMMENT '会话类型：1单聊 2群聊';

ALTER TABLE `conversation`
DROP COLUMN `status`;

ALTER TABLE `conversation_user`
    CHANGE COLUMN `user_id` `member_id` BIGINT NOT NULL COMMENT '成员ID';

-- 修改 create_user_id 字段为可空
ALTER TABLE `user`
MODIFY COLUMN `create_user_id` BIGINT(20) DEFAULT NULL COMMENT '创建人ID';

-- 修改 create_by 字段为可空
ALTER TABLE `user`
MODIFY COLUMN `create_by` VARCHAR(32) DEFAULT NULL COMMENT '创建人名称';

-- 修改 create_time 字段为可空
ALTER TABLE `user`
MODIFY COLUMN `create_time` BIGINT(20) DEFAULT NULL COMMENT '创建时间';

ALTER TABLE `friend_relation`
ADD COLUMN `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0正常，1删除'

ALTER TABLE `message`
ADD COLUMN `send_time` BIGINT NOT NULL DEFAULT 0 COMMENT '发送时间（毫秒时间戳）' AFTER `message_type`;



--表情包管理初步数显
-- 表情包/贴纸包：蘑菇头、魔性小人、默认表情等
CREATE TABLE IF NOT EXISTS emoji_pack (
                                          id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                          name VARCHAR(64) NOT NULL,
    cover_url VARCHAR(512) DEFAULT NULL,
    type TINYINT NOT NULL DEFAULT 1 COMMENT '1官方 2用户自建',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
    sort INT NOT NULL DEFAULT 0,
    created_by BIGINT UNSIGNED DEFAULT NULL COMMENT '用户自建表情包时为userId',
    created_at BIGINT NOT NULL COMMENT '毫秒时间戳',
    updated_at BIGINT NOT NULL COMMENT '毫秒时间戳',
    PRIMARY KEY (id),
    KEY idx_emoji_pack_status_sort (status, sort),
    KEY idx_emoji_pack_type (type),
    KEY idx_emoji_pack_created_by (created_by)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 表情项：支持 Unicode / 静态图 / 动图
CREATE TABLE IF NOT EXISTS emoji_item (
                                          id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                          pack_id BIGINT UNSIGNED NOT NULL,
                                          name VARCHAR(64) DEFAULT NULL,
    kind TINYINT NOT NULL COMMENT '1Unicode 2Image 3Animated',
    unicode_val VARCHAR(64) DEFAULT NULL COMMENT 'kind=1时存😀😃等',
    url VARCHAR(512) DEFAULT NULL COMMENT 'kind=2/3时存OSS/CDN URL',
    thumb_url VARCHAR(512) DEFAULT NULL COMMENT '可选：动图缩略图/预览图',
    width INT DEFAULT NULL,
    height INT DEFAULT NULL,
    size_bytes INT DEFAULT NULL,
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
    sort INT NOT NULL DEFAULT 0,
    created_at BIGINT NOT NULL COMMENT '毫秒时间戳',
    updated_at BIGINT NOT NULL COMMENT '毫秒时间戳',
    PRIMARY KEY (id),
    KEY idx_emoji_item_pack_status_sort (pack_id, status, sort),
    KEY idx_emoji_item_kind (kind),
    CONSTRAINT fk_emoji_item_pack
    FOREIGN KEY (pack_id) REFERENCES emoji_pack(id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户添加/订阅了哪些表情包（官方包/用户包都适用）
CREATE TABLE IF NOT EXISTS user_emoji_pack (
                                               id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                               user_id BIGINT UNSIGNED NOT NULL,
                                               pack_id BIGINT UNSIGNED NOT NULL,
                                               status TINYINT NOT NULL DEFAULT 1 COMMENT '1已添加 0已移除',
                                               pinned TINYINT NOT NULL DEFAULT 0 COMMENT '1置顶 0不置顶',
                                               sort INT NOT NULL DEFAULT 0 COMMENT '用户自定义排序，越小越靠前',
                                               created_at BIGINT NOT NULL COMMENT '毫秒时间戳',
                                               updated_at BIGINT NOT NULL COMMENT '毫秒时间戳',
                                               PRIMARY KEY (id),
    UNIQUE KEY uk_user_pack (user_id, pack_id),
    KEY idx_user_emoji_pack_user (user_id, status, pinned, sort),
    KEY idx_user_emoji_pack_pack (pack_id),
    CONSTRAINT fk_user_emoji_pack_pack
    FOREIGN KEY (pack_id) REFERENCES emoji_pack(id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户单独关联/收藏的表情项（不安装整个表情包）
CREATE TABLE IF NOT EXISTS user_emoji_item (
                                               id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                               user_id BIGINT UNSIGNED NOT NULL,
                                               item_id BIGINT UNSIGNED NOT NULL,
                                               status TINYINT NOT NULL DEFAULT 1 COMMENT '1已添加/收藏 0已移除',
                                               pinned TINYINT NOT NULL DEFAULT 0 COMMENT '1置顶 0不置顶',
                                               sort INT NOT NULL DEFAULT 0 COMMENT '用户自定义排序，越小越靠前',
                                               source TINYINT NOT NULL DEFAULT 1 COMMENT '1收藏消息 2上传自建 3其他',
                                               created_at BIGINT NOT NULL COMMENT '毫秒时间戳',
                                               updated_at BIGINT NOT NULL COMMENT '毫秒时间戳',
                                               PRIMARY KEY (id),
    UNIQUE KEY uk_user_item (user_id, item_id),
    KEY idx_user_emoji_item_user (user_id, status, pinned, sort),
    KEY idx_user_emoji_item_item (item_id),
    CONSTRAINT fk_user_emoji_item_item
    FOREIGN KEY (item_id) REFERENCES emoji_item(id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;