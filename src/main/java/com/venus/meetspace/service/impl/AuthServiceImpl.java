package com.venus.meetspace.service.impl;

import com.venus.meetspace.DTO.Result;
import com.venus.meetspace.entity.User;
import com.venus.meetspace.service.AuthService;
import com.venus.meetspace.util.JWTUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final JWTUtil jwtUtil;

    public AuthServiceImpl(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    public Result<User> loginSuccess() {
        return null;
    }

    @Override
    public Result<Void> loginFail() {
        return null;
    }
}
