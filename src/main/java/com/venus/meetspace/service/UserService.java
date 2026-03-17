package com.venus.meetspace.service;

import com.venus.meetspace.DTO.request.RegisterRequest;
import com.venus.meetspace.DTO.response.Profile;
import com.venus.meetspace.entity.User;



public interface UserService {
    User findById(long id);
    User findByUsername(String username);
    User register(RegisterRequest rq);
    void setProfile(Profile temp, long id); // 接受Controller发送的Profile
    Profile getProfile(long id); // 获取某用户的Profile
}
