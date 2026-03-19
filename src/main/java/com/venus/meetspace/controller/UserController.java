package com.venus.meetspace.controller;

import com.venus.meetspace.common.Result;
import com.venus.meetspace.DTO.request.AuthRequest;
import com.venus.meetspace.DTO.request.RegisterRequest;
import com.venus.meetspace.annotation.Log;
import com.venus.meetspace.service.impl.AuthServiceImpl;
import com.venus.meetspace.service.impl.UserServiceImpl;
import com.venus.meetspace.security.JwtUtil;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {
    private final UserServiceImpl usi;
    private final AuthServiceImpl asi;
    private final JwtUtil jwtUtil;

    public UserController(UserServiceImpl usi, AuthServiceImpl asi, JwtUtil jwtUtil) {
        this.usi = usi;
        this.asi = asi;
        this.jwtUtil = jwtUtil;
    }

    // 注册必须提供nickname, username, password
    @Log
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterRequest rq) {
        usi.register(rq);
        return Result.success(null, "用户创建成功！");
    }

    @PostMapping("/login")
    @Log
    public Result<String> login(@RequestBody AuthRequest authDTO) {
        String token = jwtUtil.generateUserToken(asi.login(authDTO));
        return Result.success(token, "登录成功！");
    }

    @GetMapping("/test")
    @Log
    public String test() {
        return "ok";
    }

}
