package com.example.forumspringboot27.controller;

import com.example.forumspringboot27.common.AppResult;
import com.example.forumspringboot27.common.ResultCode;
import com.example.forumspringboot27.config.AppConfig;
import com.example.forumspringboot27.exception.ApplicationException;
import com.example.forumspringboot27.model.Article;
import com.example.forumspringboot27.model.Board;
import com.example.forumspringboot27.model.User;
import com.example.forumspringboot27.service.impl.ArticleServiceImpl;
import com.example.forumspringboot27.service.impl.BoardServiceImpl;
import com.sun.org.apache.bcel.internal.generic.RET;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.bytecode.annotation.LongMemberValue;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "文章接口")
@RestController
@Slf4j
@RequestMapping("/article")
public class ArticleController {
    @Resource
    private ArticleServiceImpl articleService;

    @Resource
    private BoardServiceImpl boardService;

    /**
     * 发布新帖子
     *
     * @param bId     板块id
     * @param title   文章标题
     * @param content 文章正文
     * @return
     */
    @ApiOperation("发布新帖子")
    @PostMapping("/create")
    public AppResult create(HttpServletRequest req,
                            @ApiParam("板块id") @RequestParam("bId") @NonNull Long bId,
                            @ApiParam("标题") @RequestParam("title") @NonNull String title,
                            @ApiParam("正文") @RequestParam("content") @NonNull String content) {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if (user.getState() == 1) {
            // 表示用户禁言
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        // 分装文章对象
        Board board = boardService.selecById(bId);
        if (board == null || board.getState() == 1 || board.getDeletestate() == 1) {
            // 板块校验失败
            log.warn("板块禁用，或者板块不存在");
            return AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE);
        }
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setBoardid(bId);
        article.setUserid(user.getId());

        // 添加文章
        articleService.create(article);
        return AppResult.success();
    }


    /**
     * 如果传入id,那么查询id为id的板块的所有帖子, 否则的话查询所有的为为删除状态的帖子
     *
     * @param boardId 传入的板块的id
     * @return 返回给前端的帖子
     */
    @ApiOperation("获取帖子列表")
    @GetMapping("/getAllArticleByBoardId")
    public AppResult<List<Article>> getAllArticleByBoardId(
            @ApiParam("板块id") @RequestParam(value = "boardId", required = false) Long boardId) /* 参如果为空就查询首页的文章, 否则就查询对应板块的文章*/ {

        List<Article> list = null;
        if (boardId == null) {
            list = articleService.selectAll();
            if (list == null) {
                list = new ArrayList<>();
            }
        } else {
            list = articleService.selectAllById(boardId);
        }
        return AppResult.success(list);
    }

    @ApiOperation("根据帖子id获取详情")
    @GetMapping("/detail")
    public AppResult<Article> getDetailByAid(HttpServletRequest req, @ApiParam("帖子id") @RequestParam("aid") @NonNull Long aid) {
        // 从session中获取当前登录的用户的id
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);


        Article article = articleService.selectDetailById(aid);
        // 校验结果是否为空
        if (article == null) {
            // 返回错误信息
            return AppResult.failed(ResultCode.FAILD_ARTICLE_NOT_EXISTS);
        }
        if (user.getId() == article.getUserid()) {
            // 将作者标识 标识为true
            article.setOwn(true);
        }
        return AppResult.success(article);
    }


    @ApiOperation("修改帖子内容")
    @PostMapping("/modify")
    public AppResult modify(HttpServletRequest request,  /* 获取用户的状态, 判断是否是这个文章的作者*/
                            @ApiParam("文章id") @RequestParam("aid") @NonNull Long id,
                            @ApiParam("帖子标题") @RequestParam("title") @NonNull String title,
                            @ApiParam("帖子正文") @RequestParam("content") @NonNull String content) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if (user.getState() == 1) {
            // 返回错误描述
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        // 判断用户是不是作者
        // 先要查询帖子详情
        Article article = articleService.selectById(id);
        if (article == null || article.getState() == 1) {
            return AppResult.failed(ResultCode.FAILD_ARTICLE_NOT_EXISTS + "或者是帖子已经被删除");
        }
        if (user.getId() != article.getUserid()) {
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        articleService.modify(id, title, content);
        log.info("帖子更新成功, article id = " + id + " user id = " + user.getId());
        return AppResult.success();
    }

    @GetMapping("/thumbsUpLikeCount")
    @ApiOperation("点赞数量+1")
    public AppResult addLikeCount(HttpServletRequest request, @ApiParam("帖子id") @RequestParam("aid") @NonNull Long id) {
        HttpSession session = request.getSession(false);
        // 判断session是否有效
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        articleService.addLikeCount(id);
        return AppResult.success();
    }

    @GetMapping("/thumbsDownLikeCount")
    @ApiOperation("点赞数量-1")
    public AppResult subLikeCount(HttpServletRequest request, @ApiParam("帖子id") @RequestParam("aid") @NonNull Long id) {
        HttpSession session = request.getSession(false);
        // 判断session是否有效
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        articleService.subLikeCount(id);
        return AppResult.success();
    }

    @ApiOperation("删除帖子")
    @GetMapping("/delete")
    public AppResult deleteById(HttpServletRequest request,
                                @RequestParam("aid") @NonNull @ApiParam("帖子id") Long aid) {
        // 校验用户权限
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        Article article = articleService.selectById(aid);
        if (article == null || article.getDeletestate() == 1) {
            log.warn(ResultCode.FAILD_ARTICLE_NOT_EXISTS.getMessage());
            return AppResult.failed(ResultCode.FAILED_BOARDER_NOT_EXISTS);
        }

        if (article.getUserid().longValue() != user.getId().longValue()) {
            log.warn(ResultCode.FAILED_FORBIDDEN.getMessage());
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }

        articleService.deleteById(aid);
        return AppResult.success();
    }

    @GetMapping("/getAllArticleByUid")
    @ApiOperation("根据用户id获取所有的帖子信息")
    public AppResult<List<Article>> getAllArticleAndBoardByUserId(HttpServletRequest request,
                                                                  @ApiParam("用户id") @RequestParam(value = "uid", required = false) Long uid) {
        // 如果uid为空, 那么从session中获取id
        if (uid == null) {
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute(AppConfig.USER_SESSION);
            uid = user.getId();
        }
        List<Article> list = articleService.selectArticleAndBoardByUserId(uid);
        return AppResult.success(list);
    }

    @GetMapping("/getbinArticle")
    @ApiOperation("获取deletestate为1的artice列表")
    public AppResult getBinArticle(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        List<Article> list = articleService.selectBinArticle();
        if (list == null) {
            list = new ArrayList<>();
        }
        return AppResult.success(list);
    }

    @ApiOperation("获取被删除的文章的详情")
    @GetMapping("/getBinArticleDetail")
    public AppResult getBinArticleDetail(HttpServletRequest req,
                                         @ApiParam("帖子id") @RequestParam("aid") @NonNull Long aid) {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        Article article = articleService.selectBinArticleWithContent(aid);
        System.out.println(article);
        if (article == null) {
            return AppResult.failed(ResultCode.FAILD_ARTICLE_NOT_EXISTS + "或者是帖子已经被删除");
        }
        if (user.getId() != article.getUserid().longValue()) {
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        return AppResult.success(article);

    }

    @ApiOperation("根据文章id将deleteState修改为1")
    @GetMapping("/restate")
    public AppResult reState(HttpServletRequest req,
                        @ApiParam("帖子id") @RequestParam("aid") @NonNull Long aid) {
                // 获取用户
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        // 验证当前用户是否为article的id
        Article article = articleService.selectById(aid);
        if (article == null) {
            return AppResult.failed(ResultCode.FAILD_ARTICLE_NOT_EXISTS + "或者是帖子已经被删除");
        }
        if(user.getId() != article.getUserid().longValue()) {
            log.warn(ResultCode.FAILED_FORBIDDEN.getMessage());
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        articleService.reState(aid);
        return AppResult.success();
    }

    @ApiOperation("根据文章id 彻底删除文章")
    @GetMapping("deletetrue")
    public AppResult deleteT(HttpServletRequest req,
                             @ApiParam("帖子id") @RequestParam("aid") @NonNull Long aid) {
        // 获取用户
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        // 验证当前用户是否为article的id
        Article article = articleService.selectById(aid);
        if (article == null) {
            return AppResult.failed(ResultCode.FAILD_ARTICLE_NOT_EXISTS + "或者是帖子已经被删除");
        }
        if(user.getId() != article.getUserid().longValue()) {
            log.warn(ResultCode.FAILED_FORBIDDEN.getMessage());
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        int row = articleService.deleteArticle(aid);
        if (row != 1) {
            // 校验是否产生数据库影响
            return AppResult.failed(ResultCode.FAILED);
        }
        return AppResult.success();
    }
}
