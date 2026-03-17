package com.venus.meetspace.controller;

import com.venus.meetspace.DTO.Result;
import com.venus.meetspace.DTO.response.Profile;
import com.venus.meetspace.security.UserContext;
import com.venus.meetspace.service.impl.HomeServiceImpl;
import com.venus.meetspace.service.impl.UserServiceImpl;
import com.venus.meetspace.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    private final UserServiceImpl usi;
    private final HomeServiceImpl hsi;
    private final JwtUtil jwtUtil;

    public HomeController(UserServiceImpl usi, JwtUtil jwtUtil, HomeServiceImpl hsi) {
        this.usi = usi;
        this.jwtUtil = jwtUtil;
        this.hsi = hsi;
    }

    @GetMapping("/home")
    public Result<Profile> home(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long id = UserContext.get(); // 获取已存入的userId
        return Result.success(usi.getProfile(id), "已获取用户profile！");
    }
}
