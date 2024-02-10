package com.example.forumspringboot27.controller;

import com.example.forumspringboot27.common.AppResult;
import com.example.forumspringboot27.common.ResultCode;
import com.example.forumspringboot27.config.AppConfig;
import com.example.forumspringboot27.model.Article;
import com.example.forumspringboot27.model.ArticleReply;
import com.example.forumspringboot27.model.User;
import com.example.forumspringboot27.service.impl.ArticleReplyServiceImpl;
import com.example.forumspringboot27.service.impl.ArticleServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.zip.Adler32;

@Api(tags = "用户回复接口")
@Slf4j
@RestController
@RequestMapping("/articlereply")
public class ArticleReplyController {
    @Resource
    private ArticleServiceImpl articleService;
    @Resource
    private ArticleReplyServiceImpl articleReplyService;

    @ApiOperation("回复帖子")
    @PostMapping("/reply")
    public AppResult create(HttpServletRequest request,
                             @ApiParam("帖子id") @RequestParam("aid") @NonNull Long aid,
                            @ApiParam("回复内容") @RequestParam("content") @NonNull String content) {
        // 获取用户信息
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        if (user.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        // 用户状态正常, 校验帖子是否正常
        Article article = articleService.selectById(aid);
        if (article == null || article.getState() == 1 || article.getDeletestate() == 1) {
            return AppResult.failed(ResultCode.FAILD_ARTICLE_NOT_EXISTS + ". 或者帖子状态异常,无法访问");
        }

        // 随后就去创建回复
        ArticleReply articleReply = new ArticleReply();
        articleReply.setContent(content);
        articleReply.setPostuserid(user.getId());
        articleReply.setArticleid(aid);

        // 执行 创建回复
        articleReplyService.create(articleReply);
        return AppResult.success();
    }

    @ApiOperation("获取回复列表")
    @GetMapping("/getReplyList")
    public AppResult<List<ArticleReply>> getReplyList(@NonNull @ApiParam("帖子id") @RequestParam("aid") Long aid) {
        // 参数校验
        Article article = articleService.selectById(aid);
        if (article == null || article.getDeletestate() == 1) {
            log.warn(ResultCode.FAILD_ARTICLE_NOT_EXISTS.getMessage());
            return AppResult.failed(ResultCode.FAILED_BOARDER_NOT_EXISTS);
        }

        List<ArticleReply> list = articleReplyService.selectReplyByArticleId(aid);
        return  AppResult.success(list);
    }
}
