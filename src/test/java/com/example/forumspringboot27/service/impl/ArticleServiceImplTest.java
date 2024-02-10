package com.example.forumspringboot27.service.impl;

import com.example.forumspringboot27.dao.ArticleMapper;
import com.example.forumspringboot27.model.Article;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleServiceImplTest {
    @Resource
    private ArticleServiceImpl articleService;

    @Resource
    private ObjectMapper objectMapper;

    @Test
    void create() {
        Article article = new Article();
        article.setUserid(6L);
        article.setBoardid(1L);

        article.setTitle("单元测试");
        article.setContent("这是测试的内容");
        articleService.create(article);
        System.out.println("发帖成功");
    }

    @Test
    void selectAll() {
        List<Article> list = articleService.selectAll();
        for (Article x : list) {
            System.out.println(x);
        }
    }

    @Test
    void selectAllById() {
        List<Article> list = articleService.selectAllById(1L);
        System.out.println(list);
    }

    @Test
    void selectDetailById() throws JsonProcessingException {
        Article article = articleService.selectDetailById(1L);
        System.out.println(objectMapper.writeValueAsString(article));
    }

    @Test
    void modify() {
        articleService.modify(1L, "修改后的测试", "更新之后的测试内容");
    }

    @Test
    void addLikeCount() {
        articleService.addLikeCount(1L);
    }

    @Test
    void deleteById() {
        articleService.deleteById(1L);
    }

    @Test
    void testDeleteById() {
        articleService.deleteById(1L);
    }

    @Test
    void addOneArticleReplyCountById() {
        articleService.addOneArticleReplyCountById(2L);
    }

    @Test
    void selectArticleAndBoardByUserId() throws JsonProcessingException {
        List<Article> list = articleService.selectArticleAndBoardByUserId(6L);
        System.out.println(objectMapper.writeValueAsString(list));
    }

    @Test
    void selectBinArticleWithContent() throws JsonProcessingException {
        Article article = articleService.selectBinArticleWithContent(1L);
        System.out.println(objectMapper.writeValueAsString(article));
    }

    @Test
    void reState() {
        articleService.reState(1L);
    }

    @Test
    void testReState() {
        int row = articleService.deleteArticle(6L);
        System.out.println(row);
    }

    @Test
    void subLikeCount() {
        articleService.subLikeCount(5L);
    }
}