package com.venus.meetspace.controller;

import com.venus.meetspace.DTO.AuthDTO;
import com.venus.meetspace.DTO.RegisterRequest;
import com.venus.meetspace.DTO.Result;
import com.venus.meetspace.service.impl.AuthServiceImpl;
import com.venus.meetspace.service.impl.UserServiceImpl;
import com.venus.meetspace.security.JWTUtil;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {
    private final UserServiceImpl usi;
    private final AuthServiceImpl asi;
    private final JWTUtil jwtUtil;

    public UserController(UserServiceImpl usi, AuthServiceImpl asi, JWTUtil jwtUtil) {
        this.usi = usi;
        this.asi = asi;
        this.jwtUtil = jwtUtil;
    }

    // 注册必须提供nickname, username, password
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterRequest rq) {
        usi.register(rq);
        return Result.success(null, "用户创建成功！");
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody AuthDTO authDTO) {
        String token = jwtUtil.generateUserToken(asi.login(authDTO));
        return Result.success(token, "登录成功！");
    }

    @GetMapping("/test")
    public String test() {
        return "ok";
    }

}
