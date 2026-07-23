package com.venus.meetspace.controller;

import com.venus.meetspace.common.constant.ApiConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping(ApiConstants.HEALTH_PREFIX)
    public String health() {
        return "ok";
    }
}
