package com.example.forumspringboot27.service.impl;

import com.example.forumspringboot27.common.AppResult;
import com.example.forumspringboot27.common.ResultCode;
import com.example.forumspringboot27.dao.UserMapper;
import com.example.forumspringboot27.exception.ApplicationException;
import com.example.forumspringboot27.model.User;
import com.example.forumspringboot27.service.IUserService;

import java.util.Date;

import com.example.forumspringboot27.utils.MD5Util;
import com.example.forumspringboot27.utils.UUIDUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public void createNormalUser(User user) {
        // 判断user是否有效
        if (user == null
                || !StringUtils.hasLength(user.getUsername())
                || !StringUtils.hasLength(user.getNickname())
                || !StringUtils.hasLength(user.getPassword())
                || !StringUtils.hasLength(user.getSalt())) {
            log.info(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 查询当前传进来的是否已经存在
        User userGetinDB = userMapper.selectByUserName(user.getUsername());
        if (userGetinDB != null) {
            // 已经存在,
            log.info(ResultCode.AILED_USER_EXISTS.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.AILED_USER_EXISTS));
        }

        // 新增用户,设置默认值
        user.setGender((byte) 2);
        user.setArticlecount(0);
        user.setIsadmin((byte) 0);
        user.setDeletestate((byte) 0);
        user.setState((byte) 0);
        Date data = new Date();
        user.setCreatetime(data);
        user.setUpdatetime(data);

        // 写入数据库
        int row = userMapper.insertSelective(user);
        if (row != 1) {
            log.info(ResultCode.FAILED_CREATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }

        // 打印日志
        log.info("新增用户成功,username = " + user.getUsername() + '。');

    }

    @Override
    public User selectByUserName(String username) {
        if (!StringUtils.hasLength(username)) {
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        return userMapper.selectByUserName(username);
    }

    @Override
    public User login(String username, String password) {
        // 非空校验
        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
            throw new ApplicationException(
                    AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE)
            );
        }
        // 按用户名进行查询用户信息
        User user = selectByUserName(username);
        if (user == null) {
            // 登录失败，找不到用户
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        // 对查询的信息进行校验
        String passwordPro = MD5Util.md5Salt(password, user.getSalt());
        if (!passwordPro.equalsIgnoreCase(user.getPassword())) {
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        log.info("登录成功：username= " + username);
        return user;
    }

    @Override
    public User selectById(@NonNull Long id) {
        // 调用 seclet by primarykey的方法取查询用户对象
        User user = userMapper.selectByPrimaryKey(id);
        return user;
    }

    @Override
    public void addOneArticleCountById(Long id) {
        if (id == null || id <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_BOARDER_COUNT.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARDER_COUNT));
        }
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            log.warn(ResultCode.ERROR_IS_NULL.getMessage() + ",user id = " + id);
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        User newUser = new User();
        newUser.setId(id);
        newUser.setArticlecount(user.getArticlecount() + 1);

        // 检查mapper的操作是否生效
        int ret = userMapper.updateByPrimaryKeySelective(newUser);
        if (ret != 1) {
            log.warn(ResultCode.FAILED.toString() + "受影响的行数不为1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void subOneArticleCountById(Long uid) {
        if (uid == null || uid <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_BOARDER_COUNT.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARDER_COUNT));
        }
        User user = userMapper.selectByPrimaryKey(uid);
        if (user == null) {
            log.warn(ResultCode.ERROR_IS_NULL.getMessage() + ",user id = " + uid);
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        if (user.getArticlecount() == 0) {
            log.warn(ResultCode.FAILED_USER_ARTICLE_COUNT_ERROR.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_ARTICLE_COUNT_ERROR));
        }
        User newUser = new User();
        newUser.setId(uid);
        newUser.setArticlecount(user.getArticlecount() - 1);
        int row = userMapper.updateByPrimaryKeySelective(newUser);

        if (row != 1) {
            log.warn(ResultCode.FAILED.toString() + "受影响的行数不为1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public User modifyInfo(User user) {
        // 非空校验
        if (user == null || user.getId() == null || user.getId() <= 0) {
            log.info(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 校验用户是否存在
        User userFromDB = userMapper.selectByPrimaryKey(user.getId());
        if (userFromDB == null) {
            log.warn(ResultCode.ERROR_IS_NULL.getMessage() + ",user id = " + user.getId());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }

        boolean check = false; // 校验位, 校验所有的参数同时为空

        // 使用if 将所有的参数都校验一遍,并做处理 (防止用户设置了其他属性)
        User a = new User();  // 定义一个专门用来更新的对象
        a.setId(user.getId());
        // 当某一个参数不为空的时候, 将check设置为true
        // 参数校验
        if (StringUtils.hasLength(user.getNickname()) && !user.getNickname().equals(userFromDB.getNickname())) {
            // 修改昵称
            a.setNickname(user.getNickname());
            userFromDB.setNickname(user.getNickname());
            check = true;
        }

        if (user.getGender() != null && !user.getGender().equals(userFromDB.getGender())) {
            // 校验性别
            a.setGender(user.getGender());
            if (a.getGender() > 2 || a.getGender() < 0) {
                a.setGender((byte) 2);
            }
            userFromDB.setGender(a.getGender());
            check = true;
        }

        if (StringUtils.hasLength(user.getEmail()) && !user.getEmail().equals(userFromDB.getEmail())) {
            // 校验邮箱
            a.setEmail(user.getEmail());
            userFromDB.setEmail(user.getEmail());
            check = true;
        }

        if (StringUtils.hasLength(user.getPhonenum()) && !user.getPhonenum().equals(userFromDB.getPhonenum())) {
            // 校验电话
            a.setPhonenum(user.getPhonenum());
            userFromDB.setPhonenum(user.getPhonenum());
            check = true;
        }

        if (user.getRemark() != null && !user.getRemark().equals(userFromDB.getRemark())) {
            // 校验个人简介
            a.setRemark(user.getRemark());
            userFromDB.setRemark(user.getRemark());
            check = true;
        }

        // 查看是否允许修改
        if (!check) {
            try {
                System.out.println(new ObjectMapper().writeValueAsString(user));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            log.info(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 执行修改
        int row = userMapper.updateByPrimaryKeySelective(a);
        if (row != 1) {
            // 校验是否产生数据库影响
            log.warn(ResultCode.FAILED.toString() + "受影响的行数不为1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
        return userFromDB;
    }

    /**
     * 修改用户密码
     *
     * @param uid         用户id
     * @param newPassword 新密码
     * @param oldPassword 旧密码
     */
    @Override
    public void modifyPassword(Long uid,
                               String newPassword,
                               String oldPassword) {
        // 参数校验
        if (uid == null || uid <= 0 || !StringUtils.hasLength(newPassword) || !StringUtils.hasLength(oldPassword)) {
            // 打印日志
            log.warn(ResultCode.FAILED_BOARDER_COUNT.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARDER_COUNT));
        }

        // 查询用户信息和密码
        User user = userMapper.selectByPrimaryKey(uid);

        if (user == null || user.getDeletestate() == 1) {
            log.warn(ResultCode.ERROR_IS_NULL.getMessage() + ",user id = " + uid);
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }

        String oldSaltPassword = MD5Util.md5Salt(oldPassword, user.getSalt());

        if (!oldSaltPassword.equalsIgnoreCase(user.getPassword())) {
            log.warn(ResultCode.FAILED.getMessage() + ",user id = " + uid);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }

        String newSalt = UUIDUtil.uuid_32();
        String newSaltPassword = MD5Util.md5Salt(newPassword, newSalt);
        User update = new User();
        update.setId(uid);
        update.setSalt(newSalt);
        update.setPassword(newSaltPassword);
        int row = userMapper.updateByPrimaryKeySelective(update);

        if (row != 1) {
            // 校验是否产生数据库影响
            log.warn(ResultCode.FAILED.toString() + "受影响的行数不为1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }
}
