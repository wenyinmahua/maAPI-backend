create schema if not exists mahuaapi;
use  mahuaapi;
-- auto-generated definition
create table if not exists user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userName     varchar(256)                           null comment '用户昵称',
    userAccount  varchar(256)                           not null comment '账号',
    userAvatar   varchar(1024)                          null comment '用户头像',
    gender       tinyint                                null comment '性别',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user / admin',
    userPassword varchar(512)                           not null comment '密码',
    accessKey    varchar(512)                           not null comment 'accessKey',
    secretKey    varchar(512)                           not null comment 'secretKey',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
)
    comment '用户表';

create table if not exists interface_info
(
    id             bigint auto_increment comment 'id'
        primary key,
    userId         bigint                             not null comment '创建人id',
    name           varchar(256)                       null comment '接口名称',
    description    varchar(256)                       null comment '接口描述',
    url            varchar(512)                       null comment '请求类型',
    method         varchar(256)                       null comment '地点',
    requestHeader  varchar(512)                       null comment '请求头',
    responseHeader varchar(512)                       null comment '响应头',
    status         int      default 0                 null comment '接口状态（0 - 关闭 1 - 开启）',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除(0-未删 1 - 已删)',
    requestParams  text                               null comment '请求参数'
)
    comment '接口名称';

use  mahuaapi;
create table if not exists user_interface_info
(
    id             bigint auto_increment comment 'id'
        primary key,
    userId         bigint                             not null comment '调用者id',
    interfaceInfoId bigint                            not null comment '接口id',
    totalNum int default 0 not null comment '总调用次数',
    leftNum  int default 0 not null comment '剩余调用次数',
    status  int default 0 not null comment '0 - 正常 | 1 - 禁用',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除(0-未删 1 - 已删)'
)
    comment '用户调用接口关系表';
