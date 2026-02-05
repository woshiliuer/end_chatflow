CREATE TABLE `chat_group` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                              `group_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '群名称',
                              `introduction` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '群简介',
                              `announcement` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '群公告',
                              `owner_id` bigint NOT NULL COMMENT '群主用户ID',
                              `status` tinyint NOT NULL DEFAULT '1' COMMENT '群状态（1正常 2解散）',
                              `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                              `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
                              `create_time` bigint NOT NULL COMMENT '创建时间',
                              `update_user_id` bigint DEFAULT NULL COMMENT '更新人ID',
                              `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人名称',
                              `update_time` bigint DEFAULT NULL COMMENT '更新时间',
                              `deleted` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否删除（1未删除 2已删除）',
                              PRIMARY KEY (`id`) USING BTREE,
                              KEY `idx_owner_id` (`owner_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='群聊表';

CREATE TABLE `chat_group_user` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `group_id` bigint NOT NULL COMMENT '群聊ID',
                                   `member_id` bigint NOT NULL COMMENT '成员用户ID',
                                   `role` tinyint NOT NULL DEFAULT '1' COMMENT '角色：1成员 2管理员 3群主',
                                   `join_time` bigint NOT NULL COMMENT '加入时间（秒时间戳）',
                                   `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1正常 2已退出 3已移除',
                                   `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                                   `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
                                   `create_time` bigint NOT NULL COMMENT '创建时间',
                                   `update_user_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                   `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人名称',
                                   `update_time` bigint DEFAULT NULL COMMENT '更新时间',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE KEY `uk_group_member` (`group_id`,`member_id`) USING BTREE,
                                   KEY `idx_member_id` (`member_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='群聊成员关系表';

CREATE TABLE `conversation` (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                `conversation_type` tinyint NOT NULL COMMENT '会话类型：1单聊 2群聊',
                                `group_id` bigint DEFAULT NULL COMMENT '群聊ID（当会话类型为群聊时关联群聊表主键）',
                                `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                                `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
                                `create_time` bigint NOT NULL COMMENT '创建时间',
                                `update_user_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人名称',
                                `update_time` bigint DEFAULT NULL COMMENT '更新时间',
                                `deleted` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否删除（1未删除 2已删除）',
                                PRIMARY KEY (`id`) USING BTREE,
                                KEY `idx_group_id` (`group_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='会话表';

CREATE TABLE `conversation_user` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `conversation_id` bigint NOT NULL COMMENT '会话ID',
                                     `member_id` bigint NOT NULL COMMENT '成员ID',
                                     `status` tinyint NOT NULL DEFAULT '1' COMMENT '成员状态：1 常规 2 隐藏 3 常用',
                                     `last_read_seq` bigint NOT NULL DEFAULT '0' COMMENT '最后已读消息序号',
                                     `visible_seq` bigint NOT NULL DEFAULT '0' COMMENT '消息可见起点',
                                     `last_read_time` bigint NOT NULL DEFAULT '0' COMMENT '最后已读时间（毫秒时间戳）',
                                     `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                                     `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
                                     `create_time` bigint NOT NULL COMMENT '创建时间',
                                     `update_user_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                     `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人名称',
                                     `update_time` bigint DEFAULT NULL COMMENT '更新时间',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     UNIQUE KEY `uk_conversation_user` (`conversation_id`,`member_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='会话用户关系表';

CREATE TABLE `emoji_item` (
                              `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                              `pack_id` bigint unsigned DEFAULT NULL COMMENT '所属表情包ID',
                              `name` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表情项名称',
                              `type` tinyint NOT NULL COMMENT '表情类型：1Unicode 2静态图 3动图',
                              `unicode_val` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Unicode表情字符',
                              `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                              `create_by` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人名称',
                              `create_time` bigint NOT NULL COMMENT '创建时间',
                              `update_user_id` bigint DEFAULT NULL COMMENT '更新人ID',
                              `update_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人名称',
                              `update_time` bigint DEFAULT NULL COMMENT '更新时间',
                              `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1     DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表情项表';

CREATE TABLE `emoji_pack` (
                              `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                              `name` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表情包名称',
                              `type` tinyint NOT NULL DEFAULT '1' COMMENT '表情包类型：1-默认 2-自定义 3-官方',
                              `create_user_id` bigint DEFAULT NULL COMMENT '创建人ID',
                              `create_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人名称',
                              `create_time` bigint NOT NULL COMMENT '创建时间',
                              `update_user_id` bigint DEFAULT NULL COMMENT '更新人ID',
                              `update_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人名称',
                              `update_time` bigint DEFAULT NULL COMMENT '更新时间',
                              `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表情包表';

CREATE TABLE `favorite_item` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `user_id` bigint NOT NULL COMMENT '所属用户ID',
                                 `item_type` tinyint NOT NULL COMMENT '收藏类型：1文本 2表情',
                                 `text_content` text COMMENT '文本内容',
                                 `source_type` tinyint NOT NULL COMMENT '来源类型：1单聊 2群聊 3其他',
                                 `sender_id` bigint DEFAULT NULL COMMENT '发送人ID',
                                 `sender_name` varchar(100) DEFAULT NULL COMMENT '发送人名称',
                                 `send_time` bigint DEFAULT NULL COMMENT '发送时间',
                                 `group_id` bigint DEFAULT NULL COMMENT '群聊ID',
                                 `group_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '群聊名称',
                                 `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                                 `create_by` varchar(32) NOT NULL COMMENT '创建人名称',
                                 `create_time` bigint NOT NULL COMMENT '创建时间',
                                 `update_user_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                 `update_by` varchar(32) DEFAULT NULL COMMENT '更新人名称',
                                 `update_time` bigint DEFAULT NULL COMMENT '更新时间',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户收藏项';

CREATE TABLE `file` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `source_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据来源类型',
                        `source_id` bigint NOT NULL COMMENT '来源Id',
                        `file_type` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件类型',
                        `file_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原始文件名称',
                        `file_size` bigint DEFAULT NULL COMMENT '文件大小，单位为KB',
                        `file_path` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'oss文件路径',
                        `file_desc` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件说明',
                        `create_user_id` bigint DEFAULT NULL COMMENT '创建人ID',
                        `create_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人名称',
                        `create_time` bigint NOT NULL COMMENT '创建时间',
                        `update_user_id` bigint DEFAULT NULL COMMENT '更新人ID',
                        `update_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人名称',
                        `update_time` bigint DEFAULT NULL COMMENT '更新时间',
                        `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
                        PRIMARY KEY (`id`),
                        KEY `idx_source_type_id` (`source_type`,`source_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='文件表';

CREATE TABLE `friend_relation` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `user_id` bigint NOT NULL COMMENT '用户ID',
                                   `friend_id` bigint NOT NULL COMMENT '好友ID',
                                   `remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注/昵称，可选',
                                   `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                                   `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
                                   `create_time` bigint NOT NULL COMMENT '创建时间',
                                   `update_user_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                   `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人名称',
                                   `update_time` bigint DEFAULT NULL COMMENT '更新时间',
                                   `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：1正常，2删除',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE KEY `uk_user_friend` (`user_id`,`friend_id`) USING BTREE,
                                   KEY `idx_friend_user` (`friend_id`,`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='好友关系表';

CREATE TABLE `friend_request` (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `requester_id` bigint NOT NULL COMMENT '申请方用户ID',
                                  `receiver_id` bigint NOT NULL COMMENT '接收方用户ID',
                                  `apply_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '申请附言',
                                  `apply_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '申请备注',
                                  `request_status` tinyint NOT NULL DEFAULT '0' COMMENT '申请状态：0待处理 1已同意 2已拒绝',
                                  `handled_at` bigint DEFAULT NULL COMMENT '处理时间',
                                  `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                                  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
                                  `create_time` bigint NOT NULL COMMENT '创建时间',
                                  `update_user_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人名称',
                                  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='好友申请表';

CREATE TABLE `message` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                           `conversation_id` bigint NOT NULL COMMENT '所属会话ID',
                           `sender_id` bigint NOT NULL COMMENT '发送方用户ID',
                           `message_type` tinyint NOT NULL DEFAULT '0' COMMENT '消息类型：1文本 2表情',
                           `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '消息内容（JSON或纯文本）',
                           `sequence` bigint NOT NULL COMMENT '序号',
                           `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1正常 2撤回',
                           `send_time` bigint NOT NULL COMMENT '发送时间',
                           `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                           `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
                           `create_time` bigint NOT NULL COMMENT '创建时间',
                           `update_user_id` bigint DEFAULT NULL COMMENT '更新人ID',
                           `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人名称',
                           `update_time` bigint DEFAULT NULL COMMENT '更新时间',
                           PRIMARY KEY (`id`) USING BTREE,
                           UNIQUE KEY `uk_conversation_sequence` (`conversation_id`,`sequence`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='消息表';


CREATE TABLE `social_feed` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                               `content` text COMMENT '动态正文',
                               `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                               `create_by` varchar(64) DEFAULT NULL COMMENT '创建人名称',
                               `create_time` bigint NOT NULL COMMENT '创建时间',
                               `update_user_id` bigint DEFAULT NULL COMMENT '更新人ID',
                               `update_by` varchar(64) DEFAULT NULL COMMENT '更新人名称',
                               `update_time` bigint DEFAULT NULL COMMENT '更新时间',
                               `deleted` int NOT NULL DEFAULT '0' COMMENT '逻辑删除：0否 1是',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社交动态-主表';

CREATE TABLE `social_feed_comment` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `feed_id` bigint NOT NULL COMMENT '动态ID',
                                       `user_id` bigint NOT NULL COMMENT '评论用户ID',
                                       `content` varchar(2000) NOT NULL COMMENT '评论内容',
                                       `create_user_id` bigint NOT NULL,
                                       `create_by` varchar(64) DEFAULT NULL,
                                       `create_time` bigint NOT NULL,
                                       `update_user_id` bigint DEFAULT NULL,
                                       `update_by` varchar(64) DEFAULT NULL,
                                       `update_time` bigint DEFAULT NULL,
                                       `deleted` int NOT NULL DEFAULT '0',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社交动态-评论表';

CREATE TABLE `social_feed_like` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `feed_id` bigint NOT NULL COMMENT '动态ID',
                                    `user_id` bigint NOT NULL COMMENT '点赞用户ID',
                                    `status` tinyint NOT NULL DEFAULT '1' COMMENT '点赞状态: 1-有效, 2-取消',
                                    `create_user_id` bigint NOT NULL,
                                    `create_by` varchar(64) DEFAULT NULL,
                                    `create_time` bigint NOT NULL,
                                    `update_user_id` bigint DEFAULT NULL,
                                    `update_by` varchar(64) DEFAULT NULL,
                                    `update_time` bigint DEFAULT NULL,
                                    `deleted` int NOT NULL DEFAULT '0',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uk_post_user_status` (`feed_id`,`user_id`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社交动态-点赞表';

CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                        `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
                        `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
                        `signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '个性签名',
                        `gender` tinyint(1) DEFAULT NULL COMMENT '性别（0：未知，1：男，2：女）',
                        `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '昵称',
                        `notification_enabled` tinyint(1) NOT NULL DEFAULT '2' COMMENT '消息通知是否开启（1：关闭，2：开启）',
                        `create_user_id` bigint DEFAULT NULL COMMENT '创建人ID',
                        `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人名称',
                        `create_time` bigint DEFAULT NULL COMMENT '创建时间',
                        `update_user_id` bigint DEFAULT NULL COMMENT '更新人ID',
                        `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人名称',
                        `update_time` bigint DEFAULT NULL COMMENT '更新时间',
                        `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除（1：未删除，2：已删除）',
                        PRIMARY KEY (`id`) USING BTREE,
                        UNIQUE KEY `uk_email` (`email`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';

CREATE TABLE `user_emoji_pack` (
                                   `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
                                   `pack_id` bigint unsigned NOT NULL COMMENT '表情包ID',
                                   `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
                                   `create_user_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                   `create_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人名称',
                                   `create_time` bigint DEFAULT NULL COMMENT '创建时间',
                                   `update_user_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                   `update_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人名称',
                                   `update_time` bigint DEFAULT NULL COMMENT '更新时间',
                                   `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-表情包关系表';
