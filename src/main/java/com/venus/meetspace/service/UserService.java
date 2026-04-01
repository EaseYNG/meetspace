package com.venus.meetspace.service;

import com.venus.meetspace.dto.request.RegisterRequest;
import com.venus.meetspace.dto.Profile;
import com.venus.meetspace.entity.User;

import java.util.Optional;

public interface UserService {
    /**
     * 通过id查询用户
     * @param id 用户id
     * @return id对应用户
     */
    Optional<User> findById(long id);

    /**
     * 通过用户名查找用户
     * @param username 用户名
     * @return username对应用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 用户注册
     * @param rq 注册POST请求体
     */
    void register(RegisterRequest rq);

    /**
     * 设置用户个人信息
     * @param temp POST请求体
     * @param id 用户id
     */
    void setProfile(Profile temp, long id);

    /**
     * 通过id获取用户profile
     * @param id 用户id
     * @return profilevo
     */
    Profile getProfile(long id);

    /**
     * 保存
     * @param user 保存用户信息
     */
    void save(User user);
}
