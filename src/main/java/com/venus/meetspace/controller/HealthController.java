package com.venus.meetspace.controller;

import com.venus.meetspace.common.constant.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Service health check")
public class HealthController {

    @GetMapping(ApiConstants.HEALTH_PREFIX)
    @Operation(summary = "Health check", description = "Check if the service is running")
    public String health() {
        return "ok";
    }
}
