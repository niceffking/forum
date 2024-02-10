package com.example.forumspringboot27.service.impl;

import com.example.forumspringboot27.common.AppResult;
import com.example.forumspringboot27.common.ResultCode;
import com.example.forumspringboot27.dao.ArticleReplyMapper;
import com.example.forumspringboot27.exception.ApplicationException;
import com.example.forumspringboot27.model.ArticleReply;
import com.example.forumspringboot27.service.IArticleReplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ArticleReplyServiceImpl implements IArticleReplyService {
    @Resource
    private ArticleReplyMapper articleReplyMapper;

    @Resource
    private ArticleServiceImpl articleService;


    @Override
    public void create(ArticleReply articleReply) {
        // 非空校验
        if (articleReply == null
                || articleReply.getArticleid() == null
                || articleReply.getPostuserid() == null
                || !StringUtils.hasLength(articleReply.getContent())) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 进行插入操作
        // 设置默认值
        articleReply.setReplyuserid(null);
        articleReply.setReplyid(null);
        articleReply.setLikecount(0);
        articleReply.setState((byte) 0);
        articleReply.setDeletestate((byte) 0);
        Date date = new Date();
        articleReply.setCreatetime(date);
        articleReply.setUpdatetime(date);

        // 写入数据库
        int row = articleReplyMapper.insertSelective(articleReply);

        // 校验
        if (row != 1) {
            // 校验是否产生数据库影响
            log.warn(ResultCode.FAILED.toString() + "受影响的行数不为1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }

        // 更新帖子表的回复数
        articleService.addOneArticleReplyCountById(articleReply.getArticleid());
        log.info("帖子回复成功 artilce id =  " + articleReply.getArticleid());

    }

    @Override
    public List<ArticleReply> selectReplyByArticleId(Long aid) {
        // 参数校验
        if (aid ==null || aid < 0) {
            // 打印日志, 参数校验失败
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        List<ArticleReply> articleReply = articleReplyMapper.selectReplyByArticleId(aid);
        return articleReply; // 谁用谁校验
    }
}
