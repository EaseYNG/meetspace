package com.venus.meetspace.service;

import com.venus.meetspace.dto.request.RegisterRequest;
import com.venus.meetspace.dto.Profile;
import com.venus.meetspace.entity.User;

import java.util.Optional;

public interface UserService {
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

}
