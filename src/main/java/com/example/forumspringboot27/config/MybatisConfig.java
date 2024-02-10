package com.example.forumspringboot27.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.forumspringboot27.dao")
public class MybatisConfig {
}
