package com.example.forumspringboot27.model;

import lombok.Data;

import java.util.Date;
@Data
public class Board {
    private Long id;

    private String name;

    private Integer articlecount;

    private Integer sort;

    private Byte state;

    private Byte deletestate;

    private Date createtime;

    private Date updatetime;
}