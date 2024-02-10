package com.example.forumspringboot27.service.impl;

import com.example.forumspringboot27.common.AppResult;
import com.example.forumspringboot27.common.ResultCode;
import com.example.forumspringboot27.dao.BoardMapper;
import com.example.forumspringboot27.exception.ApplicationException;
import com.example.forumspringboot27.model.Board;
import com.example.forumspringboot27.service.IBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class BoardServiceImpl implements IBoardService {

    @Resource
    private BoardMapper boardMapper;


    @Override
    public List<Board> selectByNum(Integer num) {
        // 非空校验
        if (num <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 调用dao
        return boardMapper.selectByNum(num);
    }

    /**
     * 增加板块的文章数(+1)
     * @param id 板块的id
     */
    @Override
    public void addOneArticleCountById(Long id) {
        if (id == null || id <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_BOARDER_COUNT.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARDER_COUNT));
        }
        Board board = boardMapper.selectByPrimaryKey(id);
        if (board == null) {
            log.warn(ResultCode.ERROR_IS_NULL.getMessage() + ",board id = " + id);
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        Board newBoard  = new Board();
        newBoard.setId(board.getId());
        newBoard.setArticlecount(board.getArticlecount() + 1);
        boardMapper.updateByPrimaryKeySelective(newBoard);
    }

    @Override
    public Board selecById(Long id) {
        if (id == null || id <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_BOARDER_COUNT.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARDER_COUNT));
        }
        return boardMapper.selectByPrimaryKey(id);
    }

    @Override
    public void subOneArticleCountById(Long bid) {
        if (bid == null || bid <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_BOARDER_COUNT.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARDER_COUNT));
        }
        Board board = boardMapper.selectByPrimaryKey(bid);
        if (board == null) {
            log.warn(ResultCode.FAILED_BOARDER_NOT_EXISTS.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARDER_NOT_EXISTS));
        }
        if (board.getState() == 1) {
            log.warn(ResultCode.FAILED_FORBIDDEN.getMessage() + "board id = " + bid);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_FORBIDDEN));
        }
        if (board.getArticlecount() == 0) {
            log.warn(ResultCode.FAILED_BOARDER_ARTICLECOUNT_ERROR.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARDER_ARTICLECOUNT_ERROR));
        }
        Board b = new Board();
        b.setId(bid);
        b.setArticlecount(board.getArticlecount() - 1);

        int row = boardMapper.updateByPrimaryKeySelective(b);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString() + "受影响的行数不为1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }
}
