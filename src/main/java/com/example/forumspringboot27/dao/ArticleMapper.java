package com.example.forumspringboot27.dao;

import com.example.forumspringboot27.model.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {
    int insert(Article row);

    int insertSelective(Article row);

    Article selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Article row);

    int updateByPrimaryKeyWithBLOBs(Article row);

    int updateByPrimaryKey(Article row);

    List<Article> selectAll();

    List<Article> selectAllById(Long bId);

    Article selectDetailById(Long aid); // 根据帖子id查询帖子的详情

    List<Article> selectArticleAndBoardByUserId(@Param("uid") Long uid);

    List<Article> selectBinArticle();
    Article selectBinArticleWithContent(@Param("aid") Long aid);

    int deleteArticle(@Param("aid") Long aid);

    List<Article> selectDetailAll();
}