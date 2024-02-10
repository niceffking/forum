package com.example.forumspringboot27.service;

import com.example.forumspringboot27.model.Message;

import java.util.List;

public interface IMessageService {
    /**
     * 发送站内信
     * @param message 站内信息对象
     */
    void create (Message message);

    int selectUnreadCount(Long rid);

    // 获取站内信列表
    List<Message> selectMessageList(Long rid);

    /**
     * 更新 指定站内信的状态
     * @param mid 站内信的id
     * @param state 站内信的状态
     */
    void updateStateOfMessage(Long mid,Byte state);

    Message selectById(Long mid);

    /**
     * 回复站内信
     * @param rMid 要回复的站内信的id
     * @param message 消息类对象
     */
    void reply(Long rMid, Message message);
}
