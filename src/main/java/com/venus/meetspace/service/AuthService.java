package com.venus.meetspace.service;

import com.venus.meetspace.dto.request.AuthRequest;
import com.venus.meetspace.entity.User;


public interface AuthService {
    /**
     * 处理用户登录
     * @param authDTO POST认证请求
     * @return 登录成功的User对象
     */
    User login(AuthRequest authDTO);
}
