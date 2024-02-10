package com.example.forumspringboot27;

import com.example.forumspringboot27.dao.UserMapper;
import com.example.forumspringboot27.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
@SpringBootTest
class BitForumApplicationTests {
    @Autowired
    private UserMapper userMapper;
    @Resource
    private DataSource dataSource;
    @Test
    public void testConnection () throws SQLException {
        System.out.println("dataSource = " + dataSource.getClass());
        Connection connection = dataSource.getConnection();
        System.out.println("connection = " + connection);
        connection.close();
    }
    @Test
    void contextLoads() {
        System.out.println("TEST: 基于Spring Boot实现前后端分离的论坛系统");
    }

    @Test
    public void testMybatis() {
        User user = userMapper.selectByPrimaryKey(1l);
        System.out.println(user.toString());
        System.out.println(user.getUsername());
    }
}
