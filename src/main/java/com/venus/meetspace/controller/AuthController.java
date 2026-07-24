package com.venus.meetspace.controller;

import com.venus.meetspace.common.result.Result;
import com.venus.meetspace.model.dto.LoginCmd;
import com.venus.meetspace.model.dto.RegisterCmd;
import com.venus.meetspace.model.vo.UserProfileVO;
import com.venus.meetspace.security.CustomUserDetails;
import com.venus.meetspace.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${meetspace.api.version}/auth")
@Slf4j
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<UserProfileVO> login(@Valid @RequestBody LoginCmd cmd,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        CustomUserDetails userDetails = authService.login(cmd, request, response);
        UserProfileVO profile = new UserProfileVO();
        profile.setId(userDetails.getId());
        profile.setNickname(userDetails.getNickname());
        profile.setUsername(userDetails.getUsername());
        return Result.success(profile, "Login successful");
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterCmd cmd) {
        authService.register(cmd);
        return Result.success(null, "Registration successful");
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return Result.success(null, "Logged out");
    }
}
