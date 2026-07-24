package com.venus.meetspace.controller;

import com.venus.meetspace.common.result.Result;
import com.venus.meetspace.model.vo.ActivityVO;
import com.venus.meetspace.security.SecurityUtil;
import com.venus.meetspace.service.ActivityParticipantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${meetspace.api.version}/users/me")
@Slf4j
public class ActivityParticipantController {
    @Autowired
    private ActivityParticipantService participantService;

    @GetMapping("/activities/participated")
    public Result<List<ActivityVO>> getParticipatedActivities() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<ActivityVO> list = participantService.getParticipatedActivities(userId);
        markAsParticipant(list);
        return Result.success(list);
    }

    @GetMapping("/activities/created")
    public Result<List<ActivityVO>> getCreatedActivities() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<ActivityVO> list = participantService.getCreatedActivities(userId);
        markAsParticipant(list);
        return Result.success(list);
    }

    @GetMapping("/activities/signed-up")
    public Result<List<ActivityVO>> getSignedUpActivities() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<ActivityVO> list = participantService.getSignedUpActivities(userId);
        markAsParticipant(list);
        return Result.success(list);
    }

    private void markAsParticipant(List<ActivityVO> list) {
        if (list != null) {
            list.forEach(vo -> vo.setIsParticipant(true));
        }
    }
}
