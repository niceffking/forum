package com.example.forumspringboot27.service.impl;

import com.example.forumspringboot27.common.AppResult;
import com.example.forumspringboot27.common.ResultCode;
import com.example.forumspringboot27.dao.ArticleMapper;
import com.example.forumspringboot27.exception.ApplicationException;
import com.example.forumspringboot27.model.Article;
import com.example.forumspringboot27.model.Board;
import com.example.forumspringboot27.model.User;
import com.example.forumspringboot27.service.IArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.zip.CheckedOutputStream;

@Service
@Slf4j
public class ArticleServiceImpl implements IArticleService {
    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private UserServiceImpl userService;

    @Resource
    private BoardServiceImpl boardService;


    @Override
    public void create(Article article) {
        if (article == null || article.getUserid() == null
            || article.getTitle().isEmpty()
            || article.getContent().isEmpty()
            || article.getBoardid() == null) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
        }
        // 将id用户的文章数+1
        User user = userService.selectById(article.getUserid());
        if (user == null) {
            // 没有找到指定的用户
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        Long id = user.getId();
        userService.addOneArticleCountById(id);

        // 将板块的文章数+1
        Board board = boardService.selecById(article.getBoardid());
        if (board == null) {
            // 没有找到指定的板块
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARDER_NOT_EXISTS));
        }
        Long bid = board.getId();
        boardService.addOneArticleCountById(bid);

        // 设置初始值
        article.setVisitcount(0); // 访问
        article.setReplycount(0); // 回复数
        article.setLikecount(0); // 点赞数
        article.setDeletestate((byte) 0); // 是否删除
        article.setState((byte) 0); // 设置状态
        Date date = new Date();
        article.setCreatetime(date);
        article.setUpdatetime(date);

        // 写入数据库
        int row = articleMapper.insertSelective(article);
        if (row != 1) {
            log.warn(ResultCode.FAILED_CREATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
    }

    /**
     * 查找所有的帖子
     * @return
     */
    @Override
    public List<Article> selectAll() {

        // 直接返回结果, 谁用谁校验
        return articleMapper.selectAll();
    }

    /**
     * 根据板块id查询所有的帖子
     * @param id 板块的帖子
     * @return 查询到的所有的帖子
     */
    public List<Article> selectAllById(Long id) {
        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        Board board = boardService.selecById(id);
        if (board == null) {
            log.warn(ResultCode.FAILED_BOARDER_NOT_EXISTS.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARDER_NOT_EXISTS.getMessage()));
        }
        List<Article> list = articleMapper.selectAllById(id);
        return list;
    }

    /**
     * 通过帖子id, 查询帖子的详细信息
     * @param aid
     * @return
     */
    @Override
    public Article selectDetailById(Long aid) {
        if (aid == null || aid <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 调用dao, 获取文章的详情
         Article article = articleMapper.selectDetailById(aid);

        // 校验查询结果
        if (article == null) {
            log.warn(ResultCode.FAILD_ARTICLE_NOT_EXISTS.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILD_ARTICLE_NOT_EXISTS.getMessage()));
        }
        // 如果检验结果没有问题, 那么将帖子的访问数量+1
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        updateArticle.setVisitcount(article.getVisitcount() + 1);
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if (row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES.getMessage()));
        }
        // 返回之前 更新 +1的数据
        article.setVisitcount(updateArticle.getVisitcount());
        return article;
    }

    /**
     * 修改文章
     * @param id 帖子id
     * @param title 帖子标题
     * @param content 帖子正文
     */
    @Override
    public void modify(Long id, String title, String content) {
        // 参数校验
        if (id ==null || id <= 0 || !StringUtils.hasLength(title) || !StringUtils.hasLength(content)) {
            // 打印日志, 参数校验失败
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 构建要更新的文章对象
        Article a = new Article();
        a.setId(id);
        a.setTitle(title);
        a.setContent(content);
        Date date = new Date();
        a.setCreatetime(date);
        a.setUpdatetime(date);

        // 调用方法进行更新
        int row = articleMapper.updateByPrimaryKeySelective(a);
        if (row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }

    }

    /**
     * 通过文章id查询文章
     * @param id
     * @return
     */
    @Override
    public Article selectById(Long id) {
        if (id ==null || id <= 0) {
            // 打印日志, 参数校验失败
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        return articleMapper.selectByPrimaryKey(id);
    }

    /**
     * 通过id来修改帖子的likecount
     * @param id
     */
    @Override
    public void addLikeCount(Long id) {
        if (id ==null || id < 0) {
            // 打印日志, 参数校验失败
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        Article a = articleMapper.selectByPrimaryKey(id);
        if (a.getDeletestate() == 1 || a == null) {
            log.warn(ResultCode.FAILD_ARTICLE_NOT_EXISTS.getMessage() + "或者帖子数据异常");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILD_ARTICLE_NOT_EXISTS.getMessage())+ "或者帖子数据异常");
        }
        Article article = new Article();
        article.setId(a.getId());
        article.setLikecount(a.getLikecount() + 1);
        int row  = articleMapper.updateByPrimaryKeySelective(article);
        if(row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES.getMessage()));
        }
    }

    public void subLikeCount(Long aid) {
        if (aid ==null || aid < 0) {
            // 打印日志, 参数校验失败
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        Article a = articleMapper.selectByPrimaryKey(aid);
        if (a.getDeletestate() == 1 || a == null) {
            log.warn(ResultCode.FAILD_ARTICLE_NOT_EXISTS.getMessage() + "或者帖子数据异常");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILD_ARTICLE_NOT_EXISTS.getMessage())+ "或者帖子数据异常");
        }
        if (a.getLikecount() == 0) {
            log.warn(ResultCode.FAILD_ARTICLE_NOT_EXISTS.getMessage() + "或者帖子数据异常");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILD_ARTICLE_NOT_EXISTS.getMessage())+ "或者帖子数据异常");
        }
        Article article = new Article();
        article.setId(a.getId());
        article.setLikecount(a.getLikecount() - 1);
        int row  = articleMapper.updateByPrimaryKeySelective(article);
        if(row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES.getMessage()));
        }
    }

    @Transactional
    @Override
    public void deleteById(Long aid) {
        if (aid ==null || aid < 0) {
            // 打印日志, 参数校验失败
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 根据id查询帖子信息
        Article article = articleMapper.selectByPrimaryKey(aid);
        if (article == null || article.getDeletestate() == 1) {
            log.warn(ResultCode.FAILD_ARTICLE_NOT_EXISTS.getMessage() + "或者帖子已经删除");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILD_ARTICLE_NOT_EXISTS));
        }
        Article updateArticle = new Article();
        updateArticle.setId(aid);
        updateArticle.setDeletestate((byte)1);

        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString() + "受影响的行数不为1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
        boardService.subOneArticleCountById(article.getBoardid());
        userService.subOneArticleCountById(article.getUserid());
    }

    @Override
    public void addOneArticleReplyCountById(Long aid) {
        if (aid ==null || aid < 0) {
            // 打印日志, 参数校验失败
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 获取帖子的记录
        Article article = articleMapper.selectByPrimaryKey(aid);
        if (article == null || article.getDeletestate() == 1) { // 校验帖子是否删除
            log.warn(ResultCode.FAILD_ARTICLE_NOT_EXISTS.getMessage() + "或者帖子已经删除");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILD_ARTICLE_NOT_EXISTS));
        }

        if (article.getState() == 1) { // 校验帖子是否异常
            log.warn(ResultCode.FAILED_FORBIDDEN.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_FORBIDDEN));
        }

        // 更新操作 : 更新两个东西, 一个是 插入一条 reply类的sql数据, 另外一个是修改 帖子的回复数量
        // 并且两个是事务处理

        Article articleNew  = new Article();
        articleNew.setId(article.getId());
        articleNew.setReplycount(article.getReplycount() + 1);

        // 执行更新操作
        int row = articleMapper.updateByPrimaryKeySelective(articleNew);
        if (row != 1) {
            // 校验是否产生数据库影响
            log.warn(ResultCode.FAILED.toString() + "受影响的行数不为1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }



    @Override
    public List<Article> selectArticleAndBoardByUserId(Long uid) {
        if (uid == null || uid <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_BOARDER_COUNT.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARDER_COUNT));
        }

        // 校验用户是否存在
        User user = userService.selectById(uid);
        if (user == null) {
            log.warn(ResultCode.ERROR_IS_NULL.getMessage() + ",user id = " + uid);
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }

        List<Article> list = articleMapper.selectArticleAndBoardByUserId(uid);

        return list;

    }

    @Override
    public List<Article> selectBinArticle() {
        return articleMapper.selectBinArticle();
    }

    @Override
    public Article selectBinArticleWithContent(Long aid) {
        if (aid ==null || aid < 0) {
            // 打印日志, 参数校验失败
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        Article article = articleMapper.selectBinArticleWithContent(aid);
        if (article == null ) { // 校验帖子是否删除
            log.warn(ResultCode.FAILD_ARTICLE_NOT_EXISTS.getMessage() + "已经删除");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILD_ARTICLE_NOT_EXISTS));
        }
        return article;
    }

    @Override
    public int deleteArticle(Long aid) {
        if (aid ==null || aid < 0) {
            // 打印日志, 参数校验失败
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        Article article = articleMapper.selectByPrimaryKey(aid);
        if (article == null ) { // 校验帖子是否删除
            log.warn(ResultCode.FAILD_ARTICLE_NOT_EXISTS.getMessage() + "已经删除");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILD_ARTICLE_NOT_EXISTS));
        }
        int row = articleMapper.deleteArticle(aid);
        if (row != 1) {
            // 校验是否产生数据库影响
            log.warn(ResultCode.FAILED.toString() + "受影响的行数不为1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }

        // 需不需要进行修改文章数量?? 不需要, 已经在reState中设置
        return 1;
    }

    @Transactional
    @Override
    public void reState(Long aid) {
        // 参数校验
        if (aid ==null || aid < 0) {
            // 打印日志, 参数校验失败
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        Article article = articleMapper.selectByPrimaryKey(aid);
        if (article == null ) { // 校验帖子是否删除
            log.warn(ResultCode.FAILD_ARTICLE_NOT_EXISTS.getMessage() + "已经删除");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILD_ARTICLE_NOT_EXISTS));
        }
        if (article.getDeletestate() == 1) {
            Article a = new Article();
            a.setId(article.getId());
            a.setDeletestate((byte) 0);
            int row = articleMapper.updateByPrimaryKeySelective(a);
            if (row != 1) {
                // 校验是否产生数据库影响
                log.warn(ResultCode.FAILED.toString() + "受影响的行数不为1");
                throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
            }

            // 更新 用户的帖子数
            userService.addOneArticleCountById(article.getUserid());
            boardService.addOneArticleCountById(article.getBoardid());
        }
    }

    @Override
    public List<Article> search(String info) {
        List<Article> list = articleMapper.selectDetailAll();
        int countSize = 10;
        int listlen = list.size();
        int countLlist = (int) Math.ceil((double) listlen / countSize);
        List<List<Article>> chunks = new ArrayList<>(countLlist);
        for (int i = 0; i < countLlist; i++) {
            int start = i * countSize;
            int end = Math.min(start + countSize, countSize);
            chunks.add(list.subList(start,end));
        }
        List<Article> result = null;
        List<FutureTask<List<Article>>> futureTasksList = new ArrayList<>(countLlist);

        return null;
    }
}
