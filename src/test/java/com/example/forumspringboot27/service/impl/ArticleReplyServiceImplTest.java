package com.example.forumspringboot27.service.impl;

import com.example.forumspringboot27.model.Article;
import com.example.forumspringboot27.model.ArticleReply;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleReplyServiceImplTest {
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private ArticleReplyServiceImpl articleReplyService;

    @Test
    void create() {
        ArticleReply articleReply = new ArticleReply();
        articleReply.setPostuserid(6L);
        articleReply.setArticleid(2L);
        articleReply.setContent("你好这是一个回复");
        articleReplyService.create(articleReply);
    }

    @Test
    void selectReplyByArticleId() throws JsonProcessingException {
        List<ArticleReply> articleReplies = articleReplyService.selectReplyByArticleId(2L);
        System.out.println(objectMapper.writeValueAsString(articleReplies));
    }
}