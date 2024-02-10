package com.example.forumspringboot27.service.impl;

import com.example.forumspringboot27.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MessageServiceImplTest {
    @Resource
    private MessageServiceImpl messageService;

    @Test
    void create() {
        Message m = new Message();
        m.setContent("你好呀");
        m.setPostuserid(6L);
        m.setReceiveuserid(7L);

        messageService.create(m);
    }

    @Test
    void selectUnreadCount() {
        int a = messageService.selectUnreadCount(7L);
        System.out.println(a);
    }

    @Test
    void selectMessageList() throws JsonProcessingException {
        List<Message> list = messageService.selectMessageList(6L);
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(list));
    }

    @Test
    void updateStateOfMessage() {
        messageService.updateStateOfMessage(1L, (byte) 0);
    }

    @Test
    void selectById() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Message message = messageService.selectById(1L);
        System.out.println(objectMapper.writeValueAsString(message));
    }

    @Test
    void reply() {
        Message message = new Message();
        message.setContent("这是niceffking给你的回复");
        message.setPostuserid(7L);  // 发送者
        message.setReceiveuserid(6L);  // 接受者
        messageService.reply(1L, message);
    }
}