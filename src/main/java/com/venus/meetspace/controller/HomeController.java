package com.venus.meetspace.controller;

import com.venus.meetspace.DTO.Result;
import com.venus.meetspace.DTO.Profile;
import com.venus.meetspace.security.UserContext;
import com.venus.meetspace.service.impl.HomeServiceImpl;
import com.venus.meetspace.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home")
public class HomeController {
    private final UserServiceImpl usi;
    private final HomeServiceImpl hsi;

    public HomeController(UserServiceImpl usi, HomeServiceImpl hsi) {
        this.usi = usi;
        this.hsi = hsi;
    }

    @GetMapping("")
    public Result<Profile> home() {
        long id = UserContext.get(); // 获取已存入的userId
        return Result.success(usi.getProfile(id), "已获取用户profile！");
    }

    @PostMapping("/profile")
    public Result<Void> setProfile(@RequestBody Profile p) {
        hsi.setProfile(p);
        return Result.success(null, "已修改Profile");
    }

    @GetMapping("/profile")
    public Result<Profile> getProfile() {
        return null;
    }
}
