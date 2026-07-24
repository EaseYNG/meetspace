package com.venus.meetspace.controller;

import com.venus.meetspace.common.result.Result;
import com.venus.meetspace.model.dto.ProfileUpdateCmd;
import com.venus.meetspace.model.vo.UserHomeVO;
import com.venus.meetspace.model.vo.UserProfileVO;
import com.venus.meetspace.service.UserService;
import com.venus.meetspace.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${meetspace.api.version}/users")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/me/home")
    public Result<UserHomeVO> getHome() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(userService.getHome(userId));
    }

    @GetMapping("/me/profile")
    public Result<UserProfileVO> getProfile() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(userService.getProfile(userId));
    }

    @PatchMapping("/me/profile")
    public Result<Void> updateProfile(@Valid @RequestBody ProfileUpdateCmd cmd) {
        Long userId = SecurityUtil.getCurrentUserId();
        userService.updateProfile(userId, cmd);
        return Result.success(null, "Profile updated");
    }
}
