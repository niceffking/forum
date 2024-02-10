package com.example.forumspringboot27.controller;

import com.example.forumspringboot27.common.AppResult;
import com.example.forumspringboot27.common.ResultCode;
import com.example.forumspringboot27.config.AppConfig;
import com.example.forumspringboot27.exception.ApplicationException;
import com.example.forumspringboot27.model.Message;
import com.example.forumspringboot27.model.User;
import com.example.forumspringboot27.service.impl.MessageServiceImpl;
import com.example.forumspringboot27.service.impl.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@RestController
@RequestMapping("/message")
@Slf4j
@Api(tags = "站内信")
public class MessageController {
    @Resource
    private MessageServiceImpl messageService;

    @Resource
    private UserServiceImpl userService;

    @ApiOperation("发送站内信")
    @PostMapping("/send")
    public AppResult send(HttpServletRequest request,
                          @ApiParam("接受者id") @RequestParam("rid") @NonNull Long rid,
                          @ApiParam("站内信内容") @RequestParam("content") @NonNull String content) {
        // 查看当前用户的状态
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if (user.getState() == 1) {
            // 校验用户是否被禁言
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        if (user.getId() == rid.longValue()) {
            // 不能给自己发送站内信
            return AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE);
        }

        // 校验接收者是否存在
        User userR = userService.selectById(rid);
        if (userR == null || userR.getDeletestate() == 1) {
            return AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS);
        }

        // 封装对象
        Message message = new Message();
        message.setReceiveuserid(rid);
        message.setContent(content);
        message.setPostuserid(user.getId());

        messageService.create(message);
        return AppResult.success("发送成功");
    }

    @ApiOperation("获取未读取的消息数量")
    @GetMapping("/getUnreadCount")
    public AppResult<Integer> getUnreadCount(HttpServletRequest req) {
        // 获取当前登录的用户
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        // 调用service
        int ret = messageService.selectUnreadCount(user.getId());

        return AppResult.success(ret);
    }

    @ApiOperation("返回站内信消息列表")
    @GetMapping("/getAllMessage")
    public AppResult<List<Message>> selectMessageList(HttpServletRequest request) {
        // 获取当前登录用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        // 可以再次校验参数, 但是没有必要, 因为已经在service中调用了

        // 下面直接调用service即可
        List<Message> list = messageService.selectMessageList(user.getId());

        return AppResult.success(list);
    }

    @ApiOperation("更新站内信为已读(set state = 1)")
    @GetMapping("/MarkRead")
    public AppResult updateStateOfMessage(HttpServletRequest request,
                                          @ApiParam("站内信id") @RequestParam("mid") @NonNull Long mid) {
        // 首先校验这个参数 (但是这个参数已经在service中校验了, 不需要再次校验)
//        if (mid == null || mid <= 0 || state < 0 || state > 2) {
//            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
//            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
//        }

        // 然后根据mid查询站内信,
        Message message = messageService.selectById(mid);
        // 校验查询出来的站内信是否存在. deleteState已经在数据库的查询中设定了
        if (message == null) {
            return AppResult.failed(ResultCode.FAILD_MESSAGE_NOT_EXISTS);
        }
        // 查看用户id和rid是否相同

        // 首先查询用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        // 然后校验站内信是否属于自己
        if (user.getId() != message.getReceiveuserid().longValue()) {
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }

        // 调用service
        messageService.updateStateOfMessage(mid, (byte) 1);
        // 返回参数
        return AppResult.success();

    }

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 回复站内信
     *
     * @param request http请求
     * @param rMid    要回复的站内信id
     * @param content 回复的内容
     * @return
     */
    @ApiOperation("回复站内信")
    @PostMapping("/reply")
    public AppResult reply(HttpServletRequest request,
                           @ApiParam("要回复的站内信id") @RequestParam("rMid") @NonNull Long rMid,
                           @ApiParam("回复的内容") @RequestParam("content") @NonNull String content) {
        // 查询当前用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        // 查看这个用户是否被禁言
        if (user.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        // 不能给自己回复
        // 校验要回复的站内信id是否存在
        // 查询要回复的站内信
        Message message = messageService.selectById(rMid);
        try {
            System.out.println(objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        if (message == null || message.getDeletestate() == 1) {
            return AppResult.failed(ResultCode.FAILD_MESSAGE_NOT_EXISTS);
        }
        Message newMessage = new Message();
        newMessage.setContent(content);
        newMessage.setPostuserid(user.getId());  // 发送者
        newMessage.setReceiveuserid(message.getPostuserid());  // 接受者
        if (user.getId() == newMessage.getReceiveuserid().longValue()) {
            return AppResult.failed(ResultCode.FAILED);
        }
        messageService.reply(rMid, newMessage);


        return AppResult.success();
    }
}
