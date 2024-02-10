package com.example.forumspringboot27.controller;

import com.example.forumspringboot27.common.AppResult;
import com.example.forumspringboot27.common.ResultCode;
import com.example.forumspringboot27.exception.ApplicationException;
import com.example.forumspringboot27.model.Board;
import com.example.forumspringboot27.service.impl.BoardServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(tags = "板块接口")
@Slf4j
@RequestMapping("/board")
public class BoardController {
    @Value("${public.index.board-num}")
    private Integer num;

    @Resource
    private BoardServiceImpl boardService;
    /**
     * 查询首页板块列表
     * @return
     */
    @ApiOperation("获取首页板块列表")
    @GetMapping("/toplist")
    public AppResult<List<Board>> topList() {
        log.info("首页板块个数为: " + num);
        List<Board> list = boardService.selectByNum(num);
        if (list == null) {
            list = new ArrayList<>();
        }
        return AppResult.success(list);
    }

    @GetMapping("/getById")
    @ApiOperation("获取板块信息")
    public AppResult<Board> getBoardById(@ApiParam("板块id")  @RequestParam("id") @NonNull Long bId) {
        Board board = boardService.selecById(bId);
        // 参数校验
        if (board == null || board.getDeletestate() == 1) {
            log.warn(ResultCode.FAILED_BOARDER_NOT_EXISTS.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARDER_NOT_EXISTS));
        }
        return AppResult.success(board);
    }
}
