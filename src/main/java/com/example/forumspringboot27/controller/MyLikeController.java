package com.example.forumspringboot27.controller;

import com.example.forumspringboot27.common.AppResult;
import com.example.forumspringboot27.common.ResultCode;
import com.example.forumspringboot27.config.AppConfig;
import com.example.forumspringboot27.exception.ApplicationException;
import com.example.forumspringboot27.model.User;
import com.example.forumspringboot27.service.impl.MyLikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/likeCount")
@Slf4j
@Api(tags = "点赞功能")
public class MyLikeController {
    @Resource
    private MyLikeService myLikeService;

    @ApiOperation("加载点赞显示")
    @GetMapping("/readState")
    public AppResult<Boolean> showIsLike(HttpServletRequest request,
                                         @ApiParam("帖子id") @NonNull @RequestParam("aid") Long aid) {
        // 验证aid是否有效
        if (aid <= 0) {
            return AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE);
        }
        // 找到当前访问的用户,
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        // 然后调用service层去使用
        boolean ret = myLikeService.getMyLikeByUidAndCheck(user.getId(),aid);
        return AppResult.success(ret);
    }

    @ApiOperation("在数据库中插入我的喜欢")
    @GetMapping("/addMyLikeToDB")
    public AppResult<Boolean> addLikeInDB(HttpServletRequest request,
                                          @NonNull @ApiParam("文章id")@RequestParam("aid") Long aid){
        // 验证aid :
        if (aid <= 0) {
            // 打印日志, 参数校验失败
            return AppResult.failed(false);
        }
        // 拿到userid
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        long uid = user.getId();
        // 调用service
        myLikeService.inserMyLike(uid,aid);
        return AppResult.success(true);
    }

    @ApiOperation("取消点赞")
    @GetMapping("/unlike")
    public AppResult<Boolean> unlike(HttpServletRequest request,
                                          @NonNull @ApiParam("文章id")@RequestParam("aid") Long aid){
        // 验证aid :
        if (aid <= 0) {
            // 打印日志, 参数校验失败
            return AppResult.failed(false);
        }
        // 拿到userid
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        long uid = user.getId();
        // 调用service
        myLikeService.deleteByAidAndUid(uid,aid);
        return AppResult.success(true);
    }
}
