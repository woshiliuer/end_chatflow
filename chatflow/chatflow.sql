/*
 Navicat Premium Dump SQL

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3307
 Source Schema         : chatflow

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 24/11/2025 11:21:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_group
-- ----------------------------
DROP TABLE IF EXISTS `chat_group`;
CREATE TABLE `chat_group`  (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                               `group_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '群名称',
                               `group_avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '群头像URL',
                               `introduction` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '群简介',
                               `announcement` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '群公告',
                               `owner_id` bigint NOT NULL COMMENT '群主用户ID',
                               `status` tinyint NOT NULL DEFAULT 1 COMMENT '群状态（1正常 2解散）',
                               `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                               `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
                               `create_time` bigint NOT NULL COMMENT '创建时间',
                               `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
                               `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人名称',
                               `update_time` bigint NULL DEFAULT NULL COMMENT '更新时间',
                               `deleted` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否删除（1未删除 2已删除）',
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `idx_owner_id`(`owner_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '群聊表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chat_group
-- ----------------------------
INSERT INTO `chat_group` VALUES (1, '测试新建群聊', 'default-avatar/default-group.jpg', NULL, '', 2, 1, 2, '小呆呆', 1762078053, NULL, NULL, NULL, 1);
INSERT INTO `chat_group` VALUES (4, '产品讨论群', 'default-avatar/default-group.jpg', NULL, '欢迎新成员加入讨论～', 1, 1, 1, 'System', 1719900500, NULL, NULL, NULL, 1);
INSERT INTO `chat_group` VALUES (5, '出生', 'default-avatar/default-group.jpg', NULL, '', 1, 1, 1, '蒲公英', 1763624480, NULL, NULL, NULL, 1);
INSERT INTO `chat_group` VALUES (6, '出生333', 'default-avatar/default-group.jpg', '', '', 1, 1, 1, '蒲公英', 1763624704, NULL, NULL, NULL, 1);

-- ----------------------------
-- Table structure for chat_group_user
-- ----------------------------
DROP TABLE IF EXISTS `chat_group_user`;
CREATE TABLE `chat_group_user`  (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `group_id` bigint NOT NULL COMMENT '群聊ID',
                                    `member_id` bigint NOT NULL COMMENT '成员用户ID',
                                    `role` tinyint NOT NULL DEFAULT 1 COMMENT '角色：1成员 2管理员 3群主',
                                    `join_time` bigint NOT NULL COMMENT '加入时间（秒时间戳）',
                                    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1正常 2已退出 3已移除',
                                    `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                                    `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
                                    `create_time` bigint NOT NULL COMMENT '创建时间',
                                    `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
                                    `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人名称',
                                    `update_time` bigint NULL DEFAULT NULL COMMENT '更新时间',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `uk_group_member`(`group_id` ASC, `member_id` ASC) USING BTREE,
                                    INDEX `idx_member_id`(`member_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '群聊成员关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_group_user
-- ----------------------------
INSERT INTO `chat_group_user` VALUES (1, 6, 1, 3, 1763624704, 1, 1, '蒲公英', 1763624704, NULL, NULL, NULL);
INSERT INTO `chat_group_user` VALUES (2, 6, 2, 1, 1763695323, 1, 1, '蒲公英', 1763624704, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for conversation
-- ----------------------------
DROP TABLE IF EXISTS `conversation`;
CREATE TABLE `conversation`  (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `conversation_type` tinyint NOT NULL COMMENT '会话类型：1单聊 2群聊',
                                 `group_id` bigint NULL DEFAULT NULL COMMENT '群聊ID（当会话类型为群聊时关联群聊表主键）',
                                 `last_message_id` bigint NULL DEFAULT NULL COMMENT '最新消息ID',
                                 `last_message_time` bigint NULL DEFAULT NULL COMMENT '最新消息时间（时间戳）',
                                 `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                                 `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
                                 `create_time` bigint NOT NULL COMMENT '创建时间',
                                 `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
                                 `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人名称',
                                 `update_time` bigint NULL DEFAULT NULL COMMENT '更新时间',
                                 `deleted` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否删除（1未删除 2已删除）',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `idx_group_id`(`group_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会话表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of conversation
-- ----------------------------
INSERT INTO `conversation` VALUES (7, 1, NULL, 36, 1763619436313, 1, 'System', 1719900000, NULL, NULL, NULL, 1);
INSERT INTO `conversation` VALUES (8, 2, 4, 9, 1719900780, 1, 'System', 1719900500, NULL, NULL, NULL, 1);
INSERT INTO `conversation` VALUES (9, 2, 5, NULL, NULL, 1, '蒲公英', 1763624480, NULL, NULL, NULL, 1);
INSERT INTO `conversation` VALUES (10, 2, 6, 37, 1763624712057, 1, '蒲公英', 1763624704, NULL, NULL, NULL, 1);

-- ----------------------------
-- Table structure for conversation_user
-- ----------------------------
DROP TABLE IF EXISTS `conversation_user`;
CREATE TABLE `conversation_user`  (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                      `conversation_id` bigint NOT NULL COMMENT '会话ID',
                                      `member_id` bigint NOT NULL COMMENT '成员ID',
                                      `status` tinyint NOT NULL DEFAULT 1 COMMENT '成员状态：1 常规 2 隐藏 3 常用',
                                      `last_read_seq` bigint NOT NULL DEFAULT 0 COMMENT '最后已读消息序号',
                                      `last_read_time` bigint NOT NULL DEFAULT 0 COMMENT '最后已读时间（毫秒时间戳）',
                                      `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                                      `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
                                      `create_time` bigint NOT NULL COMMENT '创建时间',
                                      `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
                                      `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人名称',
                                      `update_time` bigint NULL DEFAULT NULL COMMENT '更新时间',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      UNIQUE INDEX `uk_conversation_user`(`conversation_id` ASC, `member_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会话用户关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of conversation_user
-- ----------------------------
INSERT INTO `conversation_user` VALUES (13, 7, 1, 1, 12, 1763619439639, 1, 'System', 1719900000, NULL, NULL, NULL);
INSERT INTO `conversation_user` VALUES (14, 7, 2, 1, 12, 1763691087748, 1, 'System', 1719900005, NULL, NULL, NULL);
INSERT INTO `conversation_user` VALUES (15, 8, 1, 2, 4, 1762165072420, 1, 'System', 1719900500, NULL, NULL, NULL);
INSERT INTO `conversation_user` VALUES (16, 8, 2, 1, 4, 1762165319152, 1, 'System', 1719900510, NULL, NULL, NULL);
INSERT INTO `conversation_user` VALUES (17, 8, 3, 1, 1, 1719900620, 1, 'System', 1719900520, NULL, NULL, NULL);
INSERT INTO `conversation_user` VALUES (18, 9, 1, 2, 0, 0, 1, '蒲公英', 1763624481, NULL, NULL, NULL);
INSERT INTO `conversation_user` VALUES (19, 9, 2, 1, 0, 0, 1, '蒲公英', 1763624481, NULL, NULL, NULL);
INSERT INTO `conversation_user` VALUES (20, 10, 1, 1, 1, 1763624713184, 1, '蒲公英', 1763624704, NULL, NULL, NULL);
INSERT INTO `conversation_user` VALUES (21, 10, 2, 1, 1, 1763691088153, 1, '蒲公英', 1763624704, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for friend_relation
-- ----------------------------
DROP TABLE IF EXISTS `friend_relation`;
CREATE TABLE `friend_relation`  (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `user_id` bigint NOT NULL COMMENT '用户ID',
                                    `friend_id` bigint NOT NULL COMMENT '好友ID',
                                    `remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注/昵称，可选',
                                    `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                                    `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
                                    `create_time` bigint NOT NULL COMMENT '创建时间',
                                    `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
                                    `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人名称',
                                    `update_time` bigint NULL DEFAULT NULL COMMENT '更新时间',
                                    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：1正常，2删除',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `uk_user_friend`(`user_id` ASC, `friend_id` ASC) USING BTREE,
                                    INDEX `idx_friend_user`(`friend_id` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '好友关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of friend_relation
-- ----------------------------
INSERT INTO `friend_relation` VALUES (6, 1, 2, '小呆呆', 1, '蒲公英', 1762065983, NULL, NULL, NULL, 1);
INSERT INTO `friend_relation` VALUES (7, 2, 1, '蒲公英', 1, '蒲公英', 1762065983, NULL, NULL, NULL, 1);

-- ----------------------------
-- Table structure for friend_request
-- ----------------------------
DROP TABLE IF EXISTS `friend_request`;
CREATE TABLE `friend_request`  (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `requester_id` bigint NOT NULL COMMENT '申请方用户ID',
                                   `receiver_id` bigint NOT NULL COMMENT '接收方用户ID',
                                   `apply_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申请附言',
                                   `apply_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申请备注',
                                   `request_status` tinyint NOT NULL DEFAULT 0 COMMENT '申请状态：0待处理 1已同意 2已拒绝',
                                   `handled_at` bigint NULL DEFAULT NULL COMMENT '处理时间',
                                   `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                                   `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
                                   `create_time` bigint NOT NULL COMMENT '创建时间',
                                   `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
                                   `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人名称',
                                   `update_time` bigint NULL DEFAULT NULL COMMENT '更新时间',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '好友申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of friend_request
-- ----------------------------
INSERT INTO `friend_request` VALUES (4, 2, 1, '你好，我是蒲公英', '蒲公英', 1, 1762065983, 2, '小呆呆', 1762065972, NULL, NULL, NULL);
INSERT INTO `friend_request` VALUES (5, 2, 1, '你好，我是蒲公英', '蒲公英', 1, 1762066117, 2, '小呆呆', 1762066117, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `conversation_id` bigint NOT NULL COMMENT '所属会话ID',
                            `sender_id` bigint NOT NULL COMMENT '发送方用户ID',
                            `message_type` tinyint NOT NULL DEFAULT 0 COMMENT '消息类型：1文本 2图片 3语音',
                            `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息内容（JSON或纯文本）',
                            `sequence` bigint NOT NULL COMMENT '序号',
                            `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1正常 2撤回',
                            `send_time` bigint NOT NULL COMMENT '发送时间',
                            `create_user_id` bigint NOT NULL COMMENT '创建人ID',
                            `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
                            `create_time` bigint NOT NULL COMMENT '创建时间',
                            `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
                            `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人名称',
                            `update_time` bigint NULL DEFAULT NULL COMMENT '更新时间',
                            PRIMARY KEY (`id`) USING BTREE,
                            UNIQUE INDEX `uk_conversation_sequence`(`conversation_id` ASC, `sequence` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (6, 7, 1, 1, '下午三点会议记得参加。', 1, 1, 1719900300, 1, 'System', 1719900300, NULL, NULL, NULL);
INSERT INTO `message` VALUES (7, 7, 2, 1, '收到，我提前准备一下材料。', 2, 1, 1719900360, 2, 'System', 1719900360, NULL, NULL, NULL);
INSERT INTO `message` VALUES (8, 7, 1, 1, '辛苦！会后发下会议纪要。', 3, 1, 1719900420, 1, 'System', 1719900420, NULL, NULL, NULL);
INSERT INTO `message` VALUES (9, 8, 1, 1, '大家好，今天下午统一讨论新版本。', 1, 1, 1719900600, 1, 'System', 1719900600, NULL, NULL, NULL);
INSERT INTO `message` VALUES (10, 8, 3, 1, 'OK，我把需求文档带上。', 2, 1, 1719900660, 3, 'System', 1719900660, NULL, NULL, NULL);
INSERT INTO `message` VALUES (11, 8, 2, 1, '我负责准备最新的原型图。', 3, 1, 1719900720, 2, 'System', 1719900720, NULL, NULL, NULL);
INSERT INTO `message` VALUES (12, 8, 1, 1, '辛苦大家，晚上发会议纪要。', 4, 1, 1719900780, 1, 'System', 1719900780, NULL, NULL, NULL);
INSERT INTO `message` VALUES (28, 7, 1, 1, '哈哈哈', 4, 1, 1762165076686, 1, '蒲公英', 1762165076, NULL, NULL, NULL);
INSERT INTO `message` VALUES (29, 7, 1, 1, '嘿嘿嘿', 5, 1, 1762165171665, 1, '蒲公英', 1762165171, NULL, NULL, NULL);
INSERT INTO `message` VALUES (30, 7, 2, 1, '嗯呢', 6, 1, 1762165199149, 2, '小呆呆', 1762165199, NULL, NULL, NULL);
INSERT INTO `message` VALUES (31, 7, 2, 1, '11', 7, 1, 1762165223822, 2, '小呆呆', 1762165223, NULL, NULL, NULL);
INSERT INTO `message` VALUES (32, 7, 1, 1, '啊哈哈', 8, 1, 1762165293161, 1, '蒲公英', 1762165293, NULL, NULL, NULL);
INSERT INTO `message` VALUES (33, 7, 2, 1, '黑恶hi', 9, 1, 1762165336801, 2, '小呆呆', 1762165336, NULL, NULL, NULL);
INSERT INTO `message` VALUES (34, 7, 2, 1, '嗯呢', 10, 1, 1762165405021, 2, '小呆呆', 1762165405, NULL, NULL, NULL);
INSERT INTO `message` VALUES (35, 7, 1, 1, '哈哈哈', 11, 1, 1762166053649, 1, '蒲公英', 1762166053, NULL, NULL, NULL);
INSERT INTO `message` VALUES (36, 7, 1, 1, 'hh', 12, 1, 1763619436313, 1, '蒲公英', 1763619436, NULL, NULL, NULL);
INSERT INTO `message` VALUES (37, 10, 1, 1, '1231', 1, 1, 1763624712057, 1, '蒲公英', 1763624712, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                         `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
                         `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
                         `signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个性签名',
                         `gender` tinyint(1) NULL DEFAULT NULL COMMENT '性别（0：未知，1：男，2：女）',
                         `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
                         `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
                         `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
                         `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人名称',
                         `create_time` bigint NULL DEFAULT NULL COMMENT '创建时间',
                         `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
                         `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人名称',
                         `update_time` bigint NULL DEFAULT NULL COMMENT '更新时间',
                         `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除（1：未删除，2：已删除）',
                         PRIMARY KEY (`id`) USING BTREE,
                         UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '17308641782@163.com', '$2a$10$AFhTDrS3D7.fGNbQMbI8A.cPytYtA1PSwHcYIBuACmWcZWeO0piD2', '我是火影高手', 1, '蒲公英', 'avatar/1_1762134168885_4736.jpg', NULL, NULL, NULL, NULL, NULL, NULL, 1);
INSERT INTO `user` VALUES (2, '7JI5NOpYkG@yun.pics', '$2a$10$YERJUlLFmPuEvCh0yqsgied6GqLoWaZ.Xv9exQOm9TYq1K7Yk/h5W', '', 1, '小呆呆', 'default-avatar/default-person.jpg', NULL, NULL, NULL, NULL, NULL, NULL, 1);
INSERT INTO `user` VALUES (3, 'p8XnmRwp4C@say0.com', '$2a$10$Azm8hKeKHxlue0AlmnFU3.IBDJCUq7T0PZ4L/MgojvVXrG/nQZQZW', '', 1, '刘二', 'default-avatar/default-person.jpg', NULL, NULL, NULL, NULL, NULL, NULL, 1);

SET FOREIGN_KEY_CHECKS = 1;
