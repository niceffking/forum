ALTER TABLE `forum_db`.`t_article_reply`
    CHANGE COLUMN `replyid` `replyid` BIGINT(20) NULL COMMENT '楼中楼' ,
    CHANGE COLUMN `replyuserid` `replyuserid` BIGINT(20) NULL COMMENT '楼主下的回复的用户编号' ;
