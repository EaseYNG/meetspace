package com.venus.meetspace.controller;

import com.venus.meetspace.common.constant.ApiConstants;
import com.venus.meetspace.common.result.Result;
import com.venus.meetspace.model.vo.ActivityVO;
import com.venus.meetspace.security.SecurityUtil;
import com.venus.meetspace.service.ActivityParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.USER_PREFIX + "/me")
@Slf4j
@Tag(name = "Activity Participation", description = "Query user's participation records")
public class ActivityParticipantController {

    private final ActivityParticipantService participantService;

    public ActivityParticipantController(ActivityParticipantService participantService) {
        this.participantService = participantService;
    }

    @GetMapping("/activities/participated")
    @Operation(summary = "Get participated activities", description = "All activities the user has participated in (created or signed up)")
    public Result<List<ActivityVO>> getParticipatedActivities() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<ActivityVO> list = participantService.getParticipatedActivities(userId);
        markAsParticipant(list);
        return Result.success(list);
    }

    @GetMapping("/activities/created")
    @Operation(summary = "Get created activities", description = "Activities created by the current user")
    public Result<List<ActivityVO>> getCreatedActivities() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<ActivityVO> list = participantService.getCreatedActivities(userId);
        markAsParticipant(list);
        return Result.success(list);
    }

    @GetMapping("/activities/signed-up")
    @Operation(summary = "Get signed-up activities", description = "Activities the current user has signed up for")
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
