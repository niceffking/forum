package com.example.forumspringboot27.dao;

import com.example.forumspringboot27.model.Message;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    int insert(Message row);

    int insertSelective(Message row);

    Message selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Message row);

    int updateByPrimaryKey(Message row);

    int selectUnreadCount(Long rid);

    // 根据接受者的id查询站内信
    List<Message> selectMessageList(Long rid);
}