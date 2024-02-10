# 首先删除 已经存在的数据库方式建立数据库失败
drop database if exists forum_db;

# 手动指定 字符集 后面的utf8mb4_general_ci是排序规则
create database forum_db character set utf8mb4 collate utf8mb4_general_ci;
use forum_db;
# 建表

# 用户表
drop table if exists t_user;
create table t_user (
                        id bigint primary key auto_increment comment '编号，自增主键',
                        username varchar(20) not null unique comment '用户名， 唯一',
                        password varchar(32) not null comment '加密后的密码',
                        nickname varchar(50) not null comment '昵称',
                        phonenum varchar(20) comment '手机号',
                        email varchar(50) comment '邮箱',
                        gender tinyint not null default 2 comment '0：女，1：男，2：保密',
                        salt varchar(32) not null comment '盐值',
                        avatarUrl varchar(255) comment '用户头像路径',
                        articlecount int not null default 0 comment '帖子数量',
                        isadmin tinyint not null default 0 comment '0不是管理员，1为管理员',
                        remark varchar(100) comment '自我介绍',
                        state tinyint not null default 0 comment '0正常，1禁言',
                        deletestate tinyint not null default  0 comment '是否删除：0否，1是',
                        createtime datetime not null comment '创建时间，精确到秒',
                        updatetime datetime not null comment '更新时间，精确到秒'
);

# 板块表
drop table if exists t_board;
create table t_board(
                        id bigint primary key auto_increment comment '编号，自增主键',
                        name varchar(50) not null comment '板块名',
                        articlecount int not null default 0 comment '帖子的数量',
                        sort int not null default 0 comment '排序优先级，升序',
                        state tinyint not null default 0 comment '0正常，1禁用',
                        deletestate tinyint not null default  0 comment '是否删除：0否，1是',
                        createtime datetime not null comment '创建时间，精确到秒',
                        updatetime datetime not null comment '更新时间，精确到秒'
);

# 帖子表
drop table if exists t_article;
create table t_article(
                          id bigint primary key auto_increment comment '编号，自增主键',
                          boardid bigint not null comment '编号，主键自增',
                          userid bigint not null comment '发帖人',
                          title varchar(100) not null comment '帖子标题',
                          content text not null comment '帖子内容',
                          visitcount int not null default 0 comment '访问量',
                          replycount int not null default 0 comment '回复数',
                          likecount int not null default 0 comment '点赞数',

                          state tinyint not null default 0 comment '0正常，1禁用',
                          deletestate tinyint not null default  0 comment '是否删除：0否，1是',
                          createtime datetime not null comment '创建时间，精确到秒',
                          updatetime datetime not null comment '更新时间，精确到秒'
);

# 帖子回复表
drop table if exists t_article_reply;
create table t_article_reply(
                                id bigint primary key auto_increment comment '编号，自增主键',
                                articleid bigint not null comment '关联帖子id',
                                postuserid bigint not null comment '楼主用户id',
                                replyid bigint comment '楼中楼',
                                replyuserid bigint comment '楼主下的回复的用户编号',
                                content varchar(500) not null comment '回帖子内容',
                                likecount int not null comment '回帖内容',

                                state tinyint not null default 0 comment '0正常，1禁用',
                                deletestate tinyint not null default  0 comment '是否删除：0否，1是',
                                createtime datetime not null comment '创建时间，精确到秒',
                                updatetime datetime not null comment '更新时间，精确到秒'
);

# 站内信
drop table if exists t_message;
create table t_message(
                          id bigint primary key auto_increment comment '编号，自增主键',
                          postuserid bigint not null comment '发送者',
                          receiveuserid bigint not null comment '接受者',
                          content varchar(255) not null comment '内容',
                          state tinyint not null default 0 comment '0正常，1禁用',
                          deletestate tinyint not null default  0 comment '是否删除：0否，1是',
                          createtime datetime not null comment '创建时间，精确到秒',
                          updatetime datetime not null comment '更新时间，精确到秒'
);


INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
                       `deleteState`, `createTime`, `updateTime`) VALUES (1, 'Java', 0, 1, 0, 0,
                                                                          '2023-01-14 19:02:18', '2023-01-14 19:02:18');
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
                       `deleteState`, `createTime`, `updateTime`) VALUES (2, 'C++', 0, 2, 0, 0,
                                                                          '2023-01-14 19:02:41', '2023-01-14 19:02:41');
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
                       `deleteState`, `createTime`, `updateTime`) VALUES (3, '前端技术', 0, 3, 0, 0,
                                                                          '2023-01-14 19:02:52', '2023-01-14 19:02:52');
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
                       `deleteState`, `createTime`, `updateTime`) VALUES (4, 'MySQL', 0, 4, 0, 0,
                                                                          '2023-01-14 19:03:02', '2023-01-14 19:03:02');
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
                       `deleteState`, `createTime`, `updateTime`) VALUES (5, '⾯试宝典', 0, 5, 0, 0,
                                                                          '2023-01-14 19:03:24', '2023-01-14 19:03:24');
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
                       `deleteState`, `createTime`, `updateTime`) VALUES (6, '经验分享', 0, 6, 0, 0,
                                                                          '2023-01-14 19:03:48', '2023-01-14 19:03:48');
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
                       `deleteState`, `createTime`, `updateTime`) VALUES (7, '招聘信息', 0, 7, 0, 0,
                                                                          '2023-01-25 21:25:33', '2023-01-25 21:25:33');
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
                       `deleteState`, `createTime`, `updateTime`) VALUES (8, '福利待遇', 0, 8, 0, 0,
                                                                          '2023-01-25 21:25:58', '2023-01-25 21:25:58');
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
                       `deleteState`, `createTime`, `updateTime`) VALUES (9, '灌⽔区', 0, 9, 0, 0,
                                                                          '2023-01-25 21:26:12', '2023-01-25 21:26:12');

