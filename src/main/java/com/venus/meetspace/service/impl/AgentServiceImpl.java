package com.venus.meetspace.service.impl;

import com.venus.meetspace.model.cmd.AgentRecommendCmd;
import com.venus.meetspace.model.vo.ActivityVO;
import com.venus.meetspace.model.vo.AgentRecommendVO;
import com.venus.meetspace.service.ActivityService;
import com.venus.meetspace.service.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AgentServiceImpl implements AgentService {

    private final ActivityService activityService;

    public AgentServiceImpl(ActivityService activityService) {
        this.activityService = activityService;
    }

    @Override
    public AgentRecommendVO recommend(AgentRecommendCmd cmd, Long userId) {
        log.info("Agent recommend: userId={}, interests={}, topK={}", userId, cmd.getInterests(), cmd.getTopK());

        List<ActivityVO> candidates = activityService.getReadyActivities();
        int topK = cmd.getTopK() != null && cmd.getTopK() > 0 ? cmd.getTopK() : 5;
        List<ActivityVO> recommendations = candidates.stream()
                .limit(topK)
                .toList();

        List<String> reasons = new ArrayList<>();
        for (ActivityVO activity : recommendations) {
            reasons.add("Recommended based on your interests: " + activity.getTitle());
        }

        AgentRecommendVO result = new AgentRecommendVO();
        result.setRecommendations(recommendations);
        result.setReasons(reasons);
        result.setModelUsed("mock/baseline");
        result.setConfidence(0.75);
        return result;
    }

    @Override
    public SseEmitter chat(String message, Long userId) {
        SseEmitter emitter = new SseEmitter(60_000L);

        new Thread(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .data("Hello! I am MeetSpace activity assistant. (Mock mode)\n")
                        .id("1"));
                emitter.send(SseEmitter.event()
                        .data("Your message: " + message + "\n")
                        .id("2"));
                emitter.send(SseEmitter.event()
                        .data("Waiting for RAG + LLM integration in future versions.")
                        .id("3"));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }
}
