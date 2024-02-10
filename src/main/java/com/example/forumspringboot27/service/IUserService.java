package com.example.forumspringboot27.service;

import com.example.forumspringboot27.model.Message;
import com.example.forumspringboot27.model.User;

/**
 * 用户接口
 */

public interface IUserService {
    /**
     * 创建一个普通用户
     * @param user 用户
     */
    void createNormalUser(User user);

    /**
     * 根据用户名查询信息
     * @param username 用户名
     * @return
     */
    User selectByUserName(String username);

    /**
     * 处理用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User login(String username, String password);

    /**
     * 根据id查询用户信息
     * @param id 用户的id
     * @return 返回的用户对象
     */
    User selectById (Long id);

    /**
     * 将当前id的用户的帖子数+1
     * @param id 用户id
     */
    void addOneArticleCountById(Long id);

    /**
     * 根据当前id的用户帖子数 - 1
     * @param uid 用户id
     */
    void subOneArticleCountById(Long uid);

    /**
     * 修改个人信息
     * @param user 被修改的对象
     */
    User modifyInfo(User user);

    /**
     * 修改用户密码
     * @param uid 用户id
     * @param newPassword 新密码
     * @param oldPassword 旧密码
     */
    void modifyPassword(Long uid, String newPassword, String oldPassword);


}
