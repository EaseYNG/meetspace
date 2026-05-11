package com.venus.meetspace.service;

import com.venus.meetspace.model.cmd.AgentRecommendCmd;
import com.venus.meetspace.model.vo.AgentRecommendVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AgentService {

    AgentRecommendVO recommend(AgentRecommendCmd cmd, Long userId);

    SseEmitter chat(String message, Long userId);
}
