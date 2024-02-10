package com.example.forumspringboot27.model;

import lombok.Data;

import java.util.Date;
@Data
public class Article {
    private Long id;

    private Long boardid;

    private Long userid;

    private String title;

    private Integer visitcount;

    private Integer replycount;

    private Integer likecount;

    private Byte state;

    private Byte deletestate;

    private Date createtime;

    private Date updatetime;

    private String content;

    // 判断 是否是作者
    private Boolean own;

    // 关联体
    private User user;

    private Board board;


    // 拿到用户id, 然后去mylike表中去查找是uid = id的数据, 然后去遍历这个数据,查找这个里面是否存在
    // 传进来的文章id == 从对应uid的like里面的id, 如果存在,就返回一个数据来标识这个数据
    private Boolean isMyLike;
}