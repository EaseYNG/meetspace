package com.venus.meetspace.controller;

import com.venus.meetspace.DTO.RegisterRequest;
import com.venus.meetspace.entity.User;
import com.venus.meetspace.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserServiceImpl usi;

    public UserController(UserServiceImpl usi) {
        this.usi = usi;
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest rq) {
        User user = usi.register(rq).getData();
        return user; // 返回注册成功的用户
    }

    @GetMapping("/display")
    public List<User> getAllUsers() {
        return usi.getAllUsers();
    }

    @GetMapping("/test")
    public String test() {
        return "ok";
    }

}
