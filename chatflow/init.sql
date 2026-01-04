-- auto-generated definition
create table chat_group
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    group_name       varchar(64)          not null comment '群名称',
    group_avatar_url varchar(255)         null comment '群头像URL',
    introduction     varchar(100)         null comment '群简介',
    announcement     varchar(500)         null comment '群公告',
    owner_id         bigint               not null comment '群主用户ID',
    status           tinyint    default 1 not null comment '群状态（1正常 2解散）',
    create_user_id   bigint               not null comment '创建人ID',
    create_by        varchar(32)          not null comment '创建人名称',
    create_time      bigint               not null comment '创建时间',
    update_user_id   bigint               null comment '更新人ID',
    update_by        varchar(32)          null comment '更新人名称',
    update_time      bigint               null comment '更新时间',
    deleted          tinyint(1) default 1 not null comment '是否删除（1未删除 2已删除）'
)
    comment '群聊表' row_format = DYNAMIC;

create index idx_owner_id
    on chat_group (owner_id);

-- auto-generated definition
create table chat_group_user
(
    id             bigint auto_increment comment '主键ID'
        primary key,
    group_id       bigint            not null comment '群聊ID',
    member_id      bigint            not null comment '成员用户ID',
    role           tinyint default 1 not null comment '角色：1成员 2管理员 3群主',
    join_time      bigint            not null comment '加入时间（秒时间戳）',
    status         tinyint default 1 not null comment '状态：1正常 2已退出 3已移除',
    create_user_id bigint            not null comment '创建人ID',
    create_by      varchar(32)       not null comment '创建人名称',
    create_time    bigint            not null comment '创建时间',
    update_user_id bigint            null comment '更新人ID',
    update_by      varchar(32)       null comment '更新人名称',
    update_time    bigint            null comment '更新时间',
    constraint uk_group_member
        unique (group_id, member_id)
)
    comment '群聊成员关系表' row_format = DYNAMIC;

create index idx_member_id
    on chat_group_user (member_id);

-- auto-generated definition
create table conversation
(
    id                bigint auto_increment comment '主键ID'
        primary key,
    conversation_type tinyint              not null comment '会话类型：1单聊 2群聊',
    group_id          bigint               null comment '群聊ID（当会话类型为群聊时关联群聊表主键）',
    last_message_id   bigint               null comment '最新消息ID',
    last_message_time bigint               null comment '最新消息时间（时间戳）',
    create_user_id    bigint               not null comment '创建人ID',
    create_by         varchar(32)          not null comment '创建人名称',
    create_time       bigint               not null comment '创建时间',
    update_user_id    bigint               null comment '更新人ID',
    update_by         varchar(32)          null comment '更新人名称',
    update_time       bigint               null comment '更新时间',
    deleted           tinyint(1) default 1 not null comment '是否删除（1未删除 2已删除）'
)
    comment '会话表' row_format = DYNAMIC;

create index idx_group_id
    on conversation (group_id);

-- auto-generated definition
create table conversation_user
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    conversation_id bigint            not null comment '会话ID',
    member_id       bigint            not null comment '成员ID',
    status          tinyint default 1 not null comment '成员状态：1 常规 2 隐藏 3 常用',
    last_read_seq   bigint  default 0 not null comment '最后已读消息序号',
    last_read_time  bigint  default 0 not null comment '最后已读时间（毫秒时间戳）',
    create_user_id  bigint            not null comment '创建人ID',
    create_by       varchar(32)       not null comment '创建人名称',
    create_time     bigint            not null comment '创建时间',
    update_user_id  bigint            null comment '更新人ID',
    update_by       varchar(32)       null comment '更新人名称',
    update_time     bigint            null comment '更新时间',
    constraint uk_conversation_user
        unique (conversation_id, member_id)
)
    comment '会话用户关系表' row_format = DYNAMIC;

-- auto-generated definition
create table friend_relation
(
    id             bigint auto_increment comment '主键ID'
        primary key,
    user_id        bigint               not null comment '用户ID',
    friend_id      bigint               not null comment '好友ID',
    remark         varchar(32)          null comment '备注/昵称，可选',
    create_user_id bigint               not null comment '创建人ID',
    create_by      varchar(32)          not null comment '创建人名称',
    create_time    bigint               not null comment '创建时间',
    update_user_id bigint               null comment '更新人ID',
    update_by      varchar(32)          null comment '更新人名称',
    update_time    bigint               null comment '更新时间',
    deleted        tinyint(1) default 0 not null comment '是否删除：1正常，2删除',
    constraint uk_user_friend
        unique (user_id, friend_id)
)
    comment '好友关系表' row_format = DYNAMIC;

create index idx_friend_user
    on friend_relation (friend_id, user_id);

-- auto-generated definition
create table friend_request
(
    id             bigint auto_increment comment '主键ID'
        primary key,
    requester_id   bigint            not null comment '申请方用户ID',
    receiver_id    bigint            not null comment '接收方用户ID',
    apply_message  varchar(255)      null comment '申请附言',
    apply_remark   varchar(500)      null comment '申请备注',
    request_status tinyint default 0 not null comment '申请状态：0待处理 1已同意 2已拒绝',
    handled_at     bigint            null comment '处理时间',
    create_user_id bigint            not null comment '创建人ID',
    create_by      varchar(32)       not null comment '创建人名称',
    create_time    bigint            not null comment '创建时间',
    update_user_id bigint            null comment '更新人ID',
    update_by      varchar(32)       null comment '更新人名称',
    update_time    bigint            null comment '更新时间'
)
    comment '好友申请表' row_format = DYNAMIC;

-- auto-generated definition
create table message
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    conversation_id bigint            not null comment '所属会话ID',
    sender_id       bigint            not null comment '发送方用户ID',
    message_type    tinyint default 0 not null comment '消息类型：1文本 2图片 3语音',
    content         text              null comment '消息内容（JSON或纯文本）',
    sequence        bigint            not null comment '序号',
    status          tinyint default 1 not null comment '状态：1正常 2撤回',
    send_time       bigint            not null comment '发送时间',
    create_user_id  bigint            not null comment '创建人ID',
    create_by       varchar(32)       not null comment '创建人名称',
    create_time     bigint            not null comment '创建时间',
    update_user_id  bigint            null comment '更新人ID',
    update_by       varchar(32)       null comment '更新人名称',
    update_time     bigint            null comment '更新时间',
    constraint uk_conversation_sequence
        unique (conversation_id, sequence)
)
    comment '消息表' row_format = DYNAMIC;

-- auto-generated definition
create table user
(
    id             bigint auto_increment comment '主键ID'
        primary key,
    email          varchar(50)          not null comment '邮箱',
    password       varchar(128)         not null comment '密码',
    signature      varchar(255)         null comment '个性签名',
    gender         tinyint(1)           null comment '性别（0：未知，1：男，2：女）',
    nickname       varchar(50)          null comment '昵称',
    avatar_url     varchar(255)         null comment '头像URL',
    create_user_id bigint               null comment '创建人ID',
    create_by      varchar(32)          null comment '创建人名称',
    create_time    bigint               null comment '创建时间',
    update_user_id bigint               null comment '更新人ID',
    update_by      varchar(32)          null comment '更新人名称',
    update_time    bigint               null comment '更新时间',
    deleted        tinyint(1) default 0 not null comment '是否删除',
    constraint uk_email
        unique (email)
)
    comment '用户表' row_format = DYNAMIC;



CREATE TABLE IF NOT EXISTS emoji_pack (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(15) NOT NULL COMMENT '表情包名称',
    cover_url VARCHAR(256) DEFAULT NULL COMMENT '表情包封面URL',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1官方 2用户自建 3默认内置',
    `create_user_id` BIGINT(20) NOT NULL COMMENT '创建人ID',
    `create_by` VARCHAR(32) NOT NULL COMMENT '创建人名称',
    `create_time` BIGINT(20) NOT NULL COMMENT '创建时间',
    `update_user_id` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
    `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人名称',
    `update_time` BIGINT(20) DEFAULT NULL COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表情包表';


CREATE TABLE IF NOT EXISTS emoji_item (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    pack_id BIGINT UNSIGNED NOT NULL COMMENT '所属表情包ID',
    name VARCHAR(15) DEFAULT NULL COMMENT '表情项名称',
    type TINYINT NOT NULL COMMENT '表情类型：1Unicode 2静态图 3动图',
    unicode_val VARCHAR(64) DEFAULT NULL COMMENT 'Unicode表情字符',
    url VARCHAR(512) DEFAULT NULL COMMENT '静态图、动图资源URL',
    `create_user_id` BIGINT(20) NOT NULL COMMENT '创建人ID',
    `create_by` VARCHAR(32) NOT NULL COMMENT '创建人名称',
    `create_time` BIGINT(20) NOT NULL COMMENT '创建时间',
    `update_user_id` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
    `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人名称',
    `update_time` BIGINT(20) DEFAULT NULL COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表情项表';

CREATE TABLE IF NOT EXISTS user_emoji_pack (
   id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
   user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
   pack_id BIGINT UNSIGNED NOT NULL COMMENT '表情包ID',
   sort INT NOT NULL DEFAULT 0 COMMENT '排序',
   `create_user_id` BIGINT(20) NOT NULL COMMENT '创建人ID',
   `create_by` VARCHAR(32) NOT NULL COMMENT '创建人名称',
   `create_time` BIGINT(20) NOT NULL COMMENT '创建时间',
   `update_user_id` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
   `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人名称',
   `update_time` BIGINT(20) DEFAULT NULL COMMENT '更新时间',
   `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
   PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-表情包关系表';


CREATE TABLE IF NOT EXISTS user_emoji_item (
   id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
   user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
   item_id BIGINT UNSIGNED NOT NULL COMMENT '表情项ID',
   sort INT NOT NULL DEFAULT 0 COMMENT '排序',
   `create_user_id` BIGINT(20) NOT NULL COMMENT '创建人ID',
   `create_by` VARCHAR(32) NOT NULL COMMENT '创建人名称',
   `create_time` BIGINT(20) NOT NULL COMMENT '创建时间',
   `update_user_id` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
   `update_by` VARCHAR(32) DEFAULT NULL COMMENT '更新人名称',
   `update_time` BIGINT(20) DEFAULT NULL COMMENT '更新时间',
   `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
   PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-表情项关系表';
