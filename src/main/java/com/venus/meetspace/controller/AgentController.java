package com.venus.meetspace.controller;

import com.venus.meetspace.common.constant.ApiConstants;
import com.venus.meetspace.common.result.Result;
import com.venus.meetspace.model.cmd.AgentRecommendCmd;
import com.venus.meetspace.model.vo.AgentRecommendVO;
import com.venus.meetspace.security.SecurityUtil;
import com.venus.meetspace.service.AgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping(ApiConstants.AGENT_PREFIX)
@Slf4j
@Tag(name = "AI Agent", description = "RAG+LLM powered activity recommendation (reserved)")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping("/recommendations")
    @Operation(summary = "Get activity recommendations",
            description = "Personalized activity recommendations based on user profile")
    public Result<AgentRecommendVO> recommend(@RequestBody AgentRecommendCmd cmd) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(agentService.recommend(cmd, userId));
    }

    @PostMapping("/chat")
    @Operation(summary = "Chat with agent (SSE stream)",
            description = "Streaming conversation with the AI agent")
    public SseEmitter chat(
            @RequestParam @Parameter(description = "User message") String message) {
        Long userId = SecurityUtil.getCurrentUserId();
        return agentService.chat(message, userId);
    }
}
