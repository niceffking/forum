package com.example.forumspringboot27.service.impl;

import com.example.forumspringboot27.model.MyLike;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyLikeServiceTest {
    @Resource
    private MyLikeService myLikeService;
    @Resource
    private ObjectMapper objectMapper;

    @Test
    void getMyLikeByUid() throws JsonProcessingException {
        Boolean ret = myLikeService.getMyLikeByUidAndCheck(6L,2L);
        System.out.println(objectMapper.writeValueAsString(ret));
    }

    @Test
    void inserMyLike() {
        myLikeService.inserMyLike(6L,5L);
    }

    @Test
    void deleteByAidAndUid() {
        myLikeService.deleteByAidAndUid(6L,5L);
    }
}