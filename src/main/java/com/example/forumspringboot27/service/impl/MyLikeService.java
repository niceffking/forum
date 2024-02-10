package com.example.forumspringboot27.service.impl;

import com.example.forumspringboot27.common.AppResult;
import com.example.forumspringboot27.common.ResultCode;
import com.example.forumspringboot27.dao.MyLikeMapper;
import com.example.forumspringboot27.exception.ApplicationException;
import com.example.forumspringboot27.model.Article;
import com.example.forumspringboot27.model.MyLike;
import com.example.forumspringboot27.service.IMyLikeService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
@Service
@Slf4j
public class MyLikeService implements IMyLikeService {
    @Resource
    private MyLikeMapper myLikeMapper;

    /**
     * 这个方法用来 获取我的喜欢, 然后查看传进来的文章是否在我的喜欢列表中
     * @param uid 用户的id
     * @param aid 文章的id
     * @return 是否存在
     */
    @Override
    public Boolean getMyLikeByUidAndCheck(Long uid, Long aid) {
        // 首先校验uid, 查看uid是否合法
        if (uid == null || uid <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_BOARDER_COUNT.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARDER_COUNT));
        }
        // 校验成功之后 进行查找操作
        List<MyLike> list = myLikeMapper.getMyLikeByUid(uid);

        if (list == null) {
            return  false;
        }
        for (MyLike x : list) {
            long getAidFromX = x.getAid();
            if (getAidFromX == aid) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void inserMyLike(Long uid, Long aid) {
        // 首先进来先检验参数是否合理:
        if (uid == null || uid <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_BOARDER_COUNT.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARDER_COUNT));
        }
        if (aid ==null || aid < 0) {
            // 打印日志, 参数校验失败
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 调用mapper,插入数据
        int row = myLikeMapper.inserMyLike(uid,aid);
        if (row != 1) {
            // 校验是否产生数据库影响
            log.warn(ResultCode.FAILED.toString() + "受影响的行数不为1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void deleteByAidAndUid(Long uid, Long aid) {
        // 首先进来先检验参数是否合理:
        if (uid == null || uid <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_BOARDER_COUNT.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARDER_COUNT));
        }
        if (aid ==null || aid < 0) {
            // 打印日志, 参数校验失败
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        int row = myLikeMapper.deleteByAidAndUid(uid,aid);
        if (row != 1) {
            // 校验是否产生数据库影响
            log.warn(ResultCode.FAILED.toString() + "受影响的行数不为1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }


    /**
     * 获取根据用户id获取我的点赞列表
     * @param uid 需要获取的用户id
     */
    @Override
    public List<Article> getMyLikeList(Long uid) {
        // 校验id
        if (uid == null || uid <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_BOARDER_COUNT.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARDER_COUNT));
        }
        // 调用dao
        List<MyLike> list = myLikeMapper.getMyLikeByUid(uid);
        if (list.isEmpty()) {
            log.warn(ResultCode.FAILD_MYLIKE_NOT_EXISTS.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILD_MYLIKE_NOT_EXISTS));
        }
        // 查询到所有的喜欢的文章之后遍历获得其id
        List<Long> listArticleId = new LinkedList<>();
        for(MyLike x : list) {
            listArticleId.add(x.getAid());
        }
        // 然后根据这里面的id遍历所有的article列表
        
    }
}
