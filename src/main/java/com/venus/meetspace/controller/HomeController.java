package com.venus.meetspace.controller;

import com.venus.meetspace.service.impl.UserServiceImpl;
import com.venus.meetspace.util.JWTUtil;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    private final UserServiceImpl usi;
    private final JWTUtil jwtUtil;

    public HomeController(UserServiceImpl usi, JWTUtil jwtUtil) {
        this.usi = usi;
        this.jwtUtil = jwtUtil;
    }


}
