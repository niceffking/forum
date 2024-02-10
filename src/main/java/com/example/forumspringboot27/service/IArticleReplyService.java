package com.example.forumspringboot27.service;


import com.example.forumspringboot27.model.ArticleReply;

import java.util.List;

public interface IArticleReplyService {
    /**
     * 新增回复
     * @param articleReply 要新增的对象
     */
    void  create (ArticleReply articleReply);

    List<ArticleReply> selectReplyByArticleId(Long aid);
}
