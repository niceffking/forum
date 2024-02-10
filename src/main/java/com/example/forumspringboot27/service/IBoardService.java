package com.example.forumspringboot27.service;

import com.example.forumspringboot27.model.Board;

import java.util.List;

public interface IBoardService {
    /**
     * 根据传入的num查询
     * @param num 要查询的条数
     * @return
     */
    List<Board> selectByNum(Integer num);

    /**
     * 更新板块的帖子数量
     * @param id 板块的id
     */
    void addOneArticleCountById(Long id);

    /**
     * 通过id查询板块
     * @param id 板块的id
     * @return
     */
    Board selecById(Long id);

    void subOneArticleCountById(Long bid);
}
