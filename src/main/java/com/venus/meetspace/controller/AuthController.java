package com.venus.meetspace.controller;

import com.venus.meetspace.cache.CacheService;
import com.venus.meetspace.common.constant.ApiConstants;
import com.venus.meetspace.common.result.Result;
import com.venus.meetspace.model.cmd.LoginCmd;
import com.venus.meetspace.model.cmd.RegisterCmd;
import com.venus.meetspace.model.vo.UserProfileVO;
import com.venus.meetspace.security.CustomUserDetails;
import com.venus.meetspace.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.AUTH_PREFIX) // /api/v1/auth
@Slf4j
@Tag(name = "Authentication", description = "Login, register, logout")
public class AuthController {

    private final AuthService authService;
    private final CacheService cacheService;

    public AuthController(AuthService authService, CacheService cacheService) {
        this.authService = authService;
        this.cacheService = cacheService;
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate with username and password, creates a session")
    public Result<UserProfileVO> login(@Valid @RequestBody LoginCmd cmd,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        String key = "login:attempt:" + cmd.getUsername();
        String val = cacheService.get(key, String.class);

        CustomUserDetails userDetails = authService.login(cmd, request, response);
        UserProfileVO profile = new UserProfileVO();
        profile.setId(userDetails.getId());
        profile.setNickname(userDetails.getNickname());
        profile.setUsername(userDetails.getUsername());
        return Result.success(profile, "Login successful");
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user account")
    public Result<Void> register(@Valid @RequestBody RegisterCmd cmd) {
        authService.register(cmd);
        return Result.success(null, "Registration successful");
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidate current session")
    public Result<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return Result.success(null, "Logged out");
    }
}
