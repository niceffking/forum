package com.example.forumspringboot27.dao;

import com.example.forumspringboot27.model.MyLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyLikeMapper {
    List<MyLike> getMyLikeByUid(@Param("uid") Long uid);

    int inserMyLike(@Param("uid")Long uid, @Param("aid") Long aid);

    int deleteByAidAndUid(@Param("uid")Long uid, @Param("aid") Long aid);
}
