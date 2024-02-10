package com.example.forumspringboot27.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
@Data
public class User {
    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    private String nickname;

    private String phonenum;

    private String email;

    private Byte gender;

    @JsonIgnore
    private String salt;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String avatarUrl;

    private Integer articlecount;

    private Byte isadmin;

    private String remark;

    private Byte state;

    @JsonIgnore
    private Byte deletestate;

    private Date createtime;

    private Date updatetime;

}