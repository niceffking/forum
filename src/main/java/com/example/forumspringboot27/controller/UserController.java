package com.example.forumspringboot27.controller;

import com.example.forumspringboot27.common.AppResult;
import com.example.forumspringboot27.common.ResultCode;
import com.example.forumspringboot27.config.AppConfig;
import com.example.forumspringboot27.model.User;
import com.example.forumspringboot27.service.impl.UserServiceImpl;
import com.example.forumspringboot27.utils.MD5Util;
import com.example.forumspringboot27.utils.UUIDUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.manager.util.SessionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

// 这是一个返回数据的controller
// 这个数据是在httpbody里面
@Slf4j
@RestController
// 设置路由
@RequestMapping("/user")
@Api(tags = "用户接口") // 对controller的说明
public class UserController {
    @Resource
    private UserServiceImpl userService;

    /**
     * 用户注册
     * @param username 用户名
     * @param nickname 昵称
     * @param password 密码
     * @param password2 确认密码
     * @return
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public AppResult register(@ApiParam("用户名") @RequestParam("username") @NonNull String username,
                              @ApiParam("昵称") @RequestParam("nickname") @NonNull String nickname,
                              @ApiParam("密码") @RequestParam("password") @NonNull String password,
                              @ApiParam("确认密码") @RequestParam("password2") @NonNull String password2) {
        // 非空校验
//        if (username.isEmpty()
//        ||  nickname.isEmpty()
//        ||  password.isEmpty()
//        ||  password2.isEmpty()) {
//            log.info("参数校验失败");
//            // controller层如果可以明确错误的信息，直接返回信息即可
//            return AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE);
//        }

        // 校验重复密码是否相同
        if (!password.equals(password2)) {
            log.warn(ResultCode.FAILED_TWO_PWD_NOT_SAME.getMessage());
            return AppResult.failed("两次密码提交不一致");
        }

        // 准备数据源
        User user = new User();
        user.setNickname(nickname);
        user.setUsername(username);

        // 处理密码
        String salt = UUIDUtil.uuid_32();
        // 生成密码密文
        password =  MD5Util.md5Salt(password,salt);
        user.setPassword(password);
        user.setSalt(salt);
        // 调用service方法
        userService.createNormalUser(user);
        return AppResult.success();
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 用户密码
     * @return 登录信息
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public AppResult login (@ApiParam("用户名") @RequestParam("username") String username,
                            @ApiParam("密码") @RequestParam("password") String password,
                            HttpServletRequest req) {
        // 不要相信前端, 进行非空校验
        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
            return  AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE);
        }
        // 调用Service中的登录逻辑, 返回User对象
        User user = userService.login(username,password);
        if (user == null) {
            return AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        // 如果登录成功, 设置到session对象
        HttpSession session = req.getSession(true);
        session.setAttribute(AppConfig.USER_SESSION,user);
        // 返回结果
        return AppResult.success();
    }

    /**
     * 获取用户登录信息
     * @param request
     * @param id 用户id
     * @return 当前登录的用户的信息
     */
    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public AppResult<User> getUserInfo(HttpServletRequest request,
                                       @ApiParam("用户id") @RequestParam(value = "id",required = false) Long id) {
        // 定义返回对象
        User user = null;

        // 如果id 为空 那么久从session中获取id
        if (id == null) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute(AppConfig.USER_SESSION) == null) {
                return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
            }
            user = (User) session.getAttribute(AppConfig.USER_SESSION);

        } else {  // 如果id不为空，那么就去数据库查询对象。
            user = userService.selectById(id);
            if (user == null) {
                return AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS);
            }
        }
        // 返回数据
        return AppResult.success(user);
    }

    @GetMapping("/logout")
    @ApiOperation("用户注销")
    public AppResult logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);

        // 判断 session是否为空
        if (session == null) {
            // 解绑当前对象和其对象的任何数据
            session.invalidate();
        }

        return AppResult.success("退出成功");
    }

    /**
     * 修改个人信息
     * @param nickname 用户昵称
     * @param gender 性别
     * @param email 邮箱
     * @param phonenum 电话
     * @param remark 个人简介
     * @return
     */
    @ApiOperation("修改个人信息")
    @PostMapping("/modifyInfo")
    public AppResult modifyInfo(HttpServletRequest request, // 要修改的用户id从session中获取
                                @ApiParam("用户昵称") @RequestParam(value = "nickname", required = false)  String nickname,
                                @ApiParam("性别") @RequestParam(value = "gender", required = false) Byte gender,
                                @ApiParam("邮箱") @RequestParam(value = "email", required = false) String email,
                                @ApiParam("电话") @RequestParam(value = "phonenum", required = false) String phonenum,
                                @ApiParam("个人简介") @RequestParam(value = "remark", required = false) String remark) {
        if(nickname != null && nickname.trim().isEmpty()) {
            nickname = null;
        }
        if (email != null && email.trim().isEmpty()) {
            email = null;
        }
        if (phonenum != null && phonenum.trim().isEmpty()) {
            phonenum = null;
        }
        if (remark != null && remark.trim().isEmpty()) {
            remark = null;
        }

        HttpSession session = request.getSession(false);
        User userNow = (User) session.getAttribute(AppConfig.USER_SESSION);

        User a = new User();
        a.setId(userNow.getId());
        a.setNickname(nickname);
        a.setGender(gender);
        a.setEmail(email);
        a.setPhonenum(phonenum);
        a.setRemark(remark);

        User user = userService.modifyInfo(a);

        session.setAttribute(AppConfig.USER_SESSION, user);
        return AppResult.success(user);
    }

    @ApiOperation("修改密码")
    @PostMapping("/modifypwd")
    public AppResult modifyPassword(HttpServletRequest request,
                                    @ApiParam("原密码") @RequestParam("oldp") @NonNull String oldp,
                                    @ApiParam("新密码") @RequestParam("newp") @NonNull String newp,
                                    @ApiParam("确认新密码") @RequestParam("passwordRepeat") @NonNull String passwprdRepeat) {
        // 校验原密码
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);


        // 校验两次输入
        if (!newp.equalsIgnoreCase(passwprdRepeat)) {
            // 说明两次输入的新密码不同
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_SAME);
        }

        // 调用service
        userService.modifyPassword(user.getId(),newp,oldp);
        if (session != null) {
            session.invalidate();
        }
        return AppResult.success();
    }
}
