package com.example.forumspringboot27.service.impl;

import com.example.forumspringboot27.common.AppResult;
import com.example.forumspringboot27.common.ResultCode;
import com.example.forumspringboot27.dao.MessageMapper;
import com.example.forumspringboot27.exception.ApplicationException;
import com.example.forumspringboot27.model.Message;
import com.example.forumspringboot27.model.User;
import com.example.forumspringboot27.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class MessageServiceImpl implements IMessageService {
    @Resource
    private MessageMapper messageMapper;
@Resource
private UserServiceImpl userService;
    @Override
    public void create(Message message) {
        // 非空校验
        if (message == null || message.getPostuserid() == null || message.getReceiveuserid() == null || !StringUtils.hasLength(message.getContent())) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 校验用户是否存在
        User userR = userService.selectById(message.getReceiveuserid());
        User userP = userService.selectById(message.getPostuserid());
        if (userR == null || userP == null) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }


        // 设置未读状态
        message.setState((byte) 0);
        // 设置删除状态
        message.setDeletestate((byte) 0);

        // 设置创建日期和修改如期
        Date date = new Date();
        message.setCreatetime(date);
        message.setUpdatetime(date);

        // 通过mapper插入数据
        int row = messageMapper.insertSelective(message);
        if (row != 1) {
            // 校验是否产生数据库影响
            log.warn(ResultCode.FAILED.toString() + "受影响的行数不为1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public int selectUnreadCount(Long rid) {
        // 参数校验
        if (rid == null || rid <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        Integer i = messageMapper.selectUnreadCount(rid);
        if (i == null) {
            log.warn(ResultCode.ERROR_SERVICES.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        return i;
    }

    @Override
    public List<Message> selectMessageList(Long rid) {
        // 参数校验
        if (rid == null || rid <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        List<Message> list  = messageMapper.selectMessageList(rid);
        return list;
    }

    @Override
    public void updateStateOfMessage(Long mid, Byte state) {
        // 校验参数
        if (mid == null || mid <= 0 || state < 0 || state > 2) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 构建 更新对象
        Message  a = new Message();
        a.setId(mid);
        a.setState(state);
        Date date = new Date();
        a.setUpdatetime(date);

        // 调用mapper
        int row = messageMapper.updateByPrimaryKeySelective(a);
        if (row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES.getMessage()));
        }

        // 返回参数
    }

    /**
     * 根据站内信id查询站内信的基本信息
     * @param mid 站内信id
     * @return
     */
    @Override
    public Message selectById(Long mid) {
        if (mid == null || mid <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        return messageMapper.selectByPrimaryKey(mid);
    }

    @Override
    public void reply(Long rMid, Message message) {
        // 非空校验
        if (rMid == null || rMid <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        Message messageFromDB = messageMapper.selectByPrimaryKey(rMid);
        if (messageFromDB == null || messageFromDB.getDeletestate() == 1) {
            log.warn(ResultCode.FAILD_MESSAGE_NOT_EXISTS.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILD_MESSAGE_NOT_EXISTS));
        }

        // 构建更新对象
        updateStateOfMessage(rMid, (byte) 2); // 设置已回复状态 (state = 2)

        // 回复的内容写入数据库
        create(message);
    }
}
