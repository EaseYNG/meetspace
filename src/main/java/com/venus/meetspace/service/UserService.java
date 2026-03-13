package com.venus.meetspace.service;

import com.venus.meetspace.DTO.Profile;
import com.venus.meetspace.DTO.RegisterRequest;
import com.venus.meetspace.entity.User;

import java.util.List;

public interface UserService {
    User findById(long id);
    User findByUsername(String username);
    User register(RegisterRequest rq);
    void setProfile(Profile temp, long id); // 接受Controller发送的Profile
    Profile getProfile(long id); // 获取某用户的Profile
}
