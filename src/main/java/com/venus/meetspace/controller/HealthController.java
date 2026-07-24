package com.venus.meetspace.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${meetspace.api.version}/health")
@Slf4j
public class HealthController {

    @GetMapping
    public String health() {
        log.debug("health controller: ok");
        return "ok";
    }
}
