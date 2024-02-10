package com.example.forumspringboot27.service;


import com.example.forumspringboot27.model.Article;

import java.util.List;

public interface IMyLikeService {
    /**
     * 根据用户的id,获取用户的like列表
     * @param uid 用户的id
     * @return 返回用户的like列表
     */
    Boolean getMyLikeByUidAndCheck(Long uid, Long aid);

    void inserMyLike(Long uid, Long aid);

    void deleteByAidAndUid(Long uid,Long aid);

    List<Article> getMyLikeList(Long uid);
}
