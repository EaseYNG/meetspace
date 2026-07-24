package com.venus.meetspace.controller;

import com.venus.meetspace.common.result.Result;
import com.venus.meetspace.model.dto.ActivityCreateCmd;
import com.venus.meetspace.model.dto.ActivityUpdateCmd;
import com.venus.meetspace.model.query.ActivitySearchQuery;
import com.venus.meetspace.model.vo.ActivityVO;
import com.venus.meetspace.security.SecurityUtil;
import com.venus.meetspace.service.ActivityFilterService;
import com.venus.meetspace.service.ActivityParticipantService;
import com.venus.meetspace.service.ActivityService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${meetspace.api.version}/activities")
@Slf4j
public class ActivityController {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ActivityParticipantService participantService;
    @Autowired
    private ActivityFilterService filterService;

    private ActivityVO enrichParticipant(ActivityVO vo) {
        if (vo != null) {
            Long userId = SecurityUtil.getCurrentUserId();
            vo.setIsParticipant(participantService.isParticipant(vo.getId(), userId));
        }
        return vo;
    }

    @PostMapping("/create")
    public Result<Void> createActivity(@Valid @RequestBody ActivityCreateCmd cmd) {
        Long userId = SecurityUtil.getCurrentUserId();
        activityService.createActivity(cmd, userId);
        return Result.success(null, "Activity created");
    }

    @GetMapping("/{activityId}")
    public Result<ActivityVO> getActivity(@PathVariable Long activityId) {
        return Result.success(enrichParticipant(activityService.getActivityById(activityId)));
    }

    @PatchMapping("/{activityId}")
    public Result<Void> updateActivity(
            @PathVariable Long activityId,
            @Valid @RequestBody ActivityUpdateCmd cmd) {
        activityService.updateActivity(activityId, cmd);
        return Result.success(null, "Activity updated");
    }

    @DeleteMapping("/{activityId}")
    public Result<Void> deleteActivity(@PathVariable Long activityId) {
        activityService.deleteActivity(activityId);
        return Result.success(null, "Activity deleted");
    }

    @PostMapping("/search")
    public Result<List<ActivityVO>> searchActivities(@RequestBody ActivitySearchQuery query) {
        List<ActivityVO> list = filterService.search(query);
        if (list != null) {
            list.forEach(this::enrichParticipant);
        }
        return Result.success(list);
    }

    @PostMapping("/{activityId}/participants")
    public Result<Void> participate(@PathVariable Long activityId) {
        Long userId = SecurityUtil.getCurrentUserId();
        participantService.participate(activityId, userId);
        return Result.success(null, "Signed up successfully");
    }

    @DeleteMapping("/{activityId}/participants/me")
    public Result<Void> quit(@PathVariable Long activityId) {
        Long userId = SecurityUtil.getCurrentUserId();
        participantService.quit(activityId, userId);
        return Result.success(null, "Quit successfully");
    }
}
