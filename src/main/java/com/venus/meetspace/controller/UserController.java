package com.venus.meetspace.controller;

import com.venus.meetspace.DTO.AuthDTO;
import com.venus.meetspace.DTO.RegisterRequest;
import com.venus.meetspace.DTO.Result;
import com.venus.meetspace.VO.UserVO;
import com.venus.meetspace.entity.User;
import com.venus.meetspace.service.impl.AuthServiceImpl;
import com.venus.meetspace.service.impl.UserServiceImpl;
import com.venus.meetspace.util.JWTUtil;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


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
    public Result<UserVO> register(@RequestBody RegisterRequest rq) {
        UserVO vo = usi.toVO(usi.register(rq));
        return Result.success(vo, "用户创建成功！");
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody AuthDTO authDTO) {
        String token = jwtUtil.generateUserToken(asi.login(authDTO));
        return Result.success(token, "登录成功！");
    }

    @GetMapping("/display")
    public Result<List<UserVO>> displayAllUsers() {
        List<UserVO> vos = new ArrayList<>();
        for(User u : usi.getAllUsers()) {
            vos.add(usi.toVO(u));
        }
        return Result.success(vos);
    }

    @GetMapping("/test")
    public String test() {
        return "ok";
    }

}
