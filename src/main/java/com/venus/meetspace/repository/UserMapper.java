package com.venus.meetspace.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.venus.meetspace.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User findByUsername(@Param("username") String username);
}
