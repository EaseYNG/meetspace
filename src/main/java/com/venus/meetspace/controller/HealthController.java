package com.venus.meetspace.controller;

import com.venus.meetspace.common.constant.ApiConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HealthController {

    @GetMapping(ApiConstants.HEALTH_PREFIX)
    public String health() {
        log.debug("health controller: ok");
        return "ok";
    }
}
