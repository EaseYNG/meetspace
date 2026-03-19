package com.venus.meetspace.util;

import com.venus.meetspace.common.Result;
import com.venus.meetspace.DTO.response.UserResponse;
import com.venus.meetspace.entity.User;
import com.venus.meetspace.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/db")
public class DbDisplay {
    private UserServiceImpl usi;

    public DbDisplay(UserServiceImpl usi) {
        this.usi = usi;
    }

    @GetMapping("/users")
    public Result<List<UserResponse>> displayAllUsers() {
        List<User> users = usi.findAll();
        List<UserResponse> responses = new ArrayList<>();
        UserResponse ur = new UserResponse();
        for(User u : users) {
            ur.setId(u.getId());
            ur.setUsername(u.getUsername());
            ur.setPassword(u.getPassword());
            ur.setNickname(u.getNickname());
            ur.setAge(u.getAge());
            ur.setGender(u.getGender());
            ur.setEmail(u.getEmail());
            ur.setFirstname(u.getFirstname());
            ur.setLastname(u.getLastname());
            responses.add(ur);
        }

        return Result.success(responses, "获取所有用户");
    }
}
