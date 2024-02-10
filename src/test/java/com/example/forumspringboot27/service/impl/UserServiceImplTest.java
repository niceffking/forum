package com.example.forumspringboot27.service.impl;

import com.example.forumspringboot27.dao.UserMapper;
import com.example.forumspringboot27.model.User;
import com.example.forumspringboot27.service.IUserService;
import com.example.forumspringboot27.utils.MD5Util;
import com.example.forumspringboot27.utils.UUIDUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {
    @Resource
    private UserServiceImpl userService;


    @Transactional
    @Test
    void createNormalUser() {
        User user = new User();
        user.setUsername("boy");
        user.setNickname("nickBOy");
        // 定义一个原始密码
        String passWord = "123456";
        // 生成盐值
        String salt = UUIDUtil.uuid_32();
        user.setSalt(salt);
        String salPassword = MD5Util.md5Salt(passWord,salt);
        user.setPassword(salPassword);
        userService.createNormalUser(user);
        // 打印结果
        System.out.println(user);
    }

    @Test
    void selectByUserName() {
        User user = userService.selectByUserName("niceff");
        System.out.println(user);
    }


    @Test
    void login() {
        User user = userService.login("admin","112");
        System.out.println(user);
    }

    @Test
    void selectById() {
        User user = userService.selectById(4L);
        System.out.println(user);
    }

    @Transactional
    @Test
    void addOneArticleCountById() {
        userService.addOneArticleCountById(4L);
        User user = userService.selectById(4L);
        System.out.println(user);
    }

    @Test
    void subOneArticleCountById() {
        userService.subOneArticleCountById(6L);
    }

    @Test
    void modifyInfo() {
        User user = new User();
        user.setId(7L);
        user.setGender((byte) 3);
        user.setPhonenum("18934635321");
        user.setNickname("niceffking");
        user.setEmail("2386389757@qq.com");
        user.setRemark("欲速则不达");
        userService.modifyInfo(user);
    }

    @Test
    void modifyPassword() {
        userService.modifyPassword(7L,"123","111");
    }
}