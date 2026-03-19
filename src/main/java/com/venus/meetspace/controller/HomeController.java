package com.venus.meetspace.controller;

import com.venus.meetspace.annotation.CurrentUserId;
import com.venus.meetspace.annotation.Log;
import com.venus.meetspace.common.Result;
import com.venus.meetspace.dto.Profile;
import com.venus.meetspace.service.impl.HomeServiceImpl;
import com.venus.meetspace.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home")
@Log
public class HomeController {
    private final UserServiceImpl usi;
    private final HomeServiceImpl hsi;

    public HomeController(UserServiceImpl usi, HomeServiceImpl hsi) {
        this.usi = usi;
        this.hsi = hsi;
    }

    @GetMapping("")
    public Result<Profile> home(@CurrentUserId Long userId) {
        return Result.success(null, "主页");
    }

    @PostMapping("/profile")
    public Result<Void> setProfile(@RequestBody Profile p, @CurrentUserId Long userId) {
        usi.setProfile(p, userId);
        return Result.success(null, "已修改用户Profile");
    }

    @GetMapping("/profile")
    public Result<Profile> getProfile(@CurrentUserId Long userId) {
        return Result.success(usi.getProfile(userId), "已获取用户Profile！");
    }


}
