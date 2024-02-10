package com.example.forumspringboot27.service.impl;

import com.example.forumspringboot27.dao.BoardMapper;
import com.example.forumspringboot27.model.Board;
import com.example.forumspringboot27.service.IBoardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class BoardServiceImplTest {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private BoardMapper boardMapper;
    @Resource
    private BoardServiceImpl boardService;
    @Test
    void selectByNum() {
        List<Board> list = boardService.selectByNum(9);
        System.out.println(list);
    }

    @Transactional
    @Test
    void addOneArticleCountById() {
        boardService.addOneArticleCountById(1L);
        Board board = boardMapper.selectByPrimaryKey(1L);
        System.out.println(board);
    }

    @Test
    void selecById() throws JsonProcessingException {
        Board board = boardService.selecById(10L);
        System.out.println(objectMapper.writeValueAsString(board));
    }

    @Test
    void subOneArticleCountById() {
        boardService.subOneArticleCountById(3L);
    }
}