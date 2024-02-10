package com.example.forumspringboot27.service;

import com.example.forumspringboot27.model.Article;
import com.example.forumspringboot27.model.Board;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IArticleService {

    /**
     * 发布帖子
     * @param article 要发布的帖子
     */
    void create(Article article);

    List<Article> selectAll();

    List<Article> selectAllById(Long id);

    Article selectDetailById(Long aid);

    /**
     * 编辑帖子
     * @param id 帖子id
     * @param title 帖子标题
     * @param content 帖子正文
     */
    void modify(Long id, String title, String content);

    Article selectById(Long id);

    void addLikeCount(Long id);

    void subLikeCount(Long aid);

    void deleteById(Long aid);

    /**
     * 文章回复数量+1
     * @param aid 文章id
     */
    void addOneArticleReplyCountById(Long aid);

//    /**
//     * 文章回复数量-1
//     * @param aid 文章id
//     */
//    void decOneArticleReplyCountById(Long aid);

    /**
     * 根据用户id查询所有的帖子
     * @param uid 用户id
     * @return
     */
    List<Article> selectArticleAndBoardByUserId(Long uid);

    /**
     * 查询被删除的帖子,这个帖子的deleteState为1
     * @return 返回被查询的列表
     */
    List<Article> selectBinArticle();

    /**
     * 根据文章id查询具有文章内容的文章
     * @param aid 帖子id
     * @return 被查询到的帖子列表
     */
    Article selectBinArticleWithContent(Long aid);

    void reState(Long aid);

    /**
     * 彻底删除文章
     * @param aid  文章id
     * @return 返回收影响的行数
     */
    int deleteArticle(Long aid);

    List<Article> search(String info);

}
