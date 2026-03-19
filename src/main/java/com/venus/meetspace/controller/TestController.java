package com.venus.meetspace.controller;

import com.venus.meetspace.annotation.Log;
import com.venus.meetspace.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Log
    @GetMapping("/log")
    public Result<String> log() {
        return Result.success(null, "ok");
    }
}
