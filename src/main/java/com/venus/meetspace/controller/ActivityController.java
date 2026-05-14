package com.venus.meetspace.controller;

import com.venus.meetspace.common.constant.ApiConstants;
import com.venus.meetspace.common.result.Result;
import com.venus.meetspace.model.cmd.ActivityCreateCmd;
import com.venus.meetspace.model.cmd.ActivityUpdateCmd;
import com.venus.meetspace.model.query.ActivitySearchQuery;
import com.venus.meetspace.model.vo.ActivityVO;
import com.venus.meetspace.security.SecurityUtil;
import com.venus.meetspace.service.ActivityFilterService;
import com.venus.meetspace.service.ActivityParticipantService;
import com.venus.meetspace.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.ACTIVITY_PREFIX) // /api/v1/activities
@Slf4j
@Tag(name = "Activity", description = "Activity CRUD, search, and participation")
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityParticipantService participantService;
    private final ActivityFilterService filterService;

    public ActivityController(ActivityService activityService,
                               ActivityParticipantService participantService,
                               ActivityFilterService filterService) {
        this.activityService = activityService;
        this.participantService = participantService;
        this.filterService = filterService;
    }

    private ActivityVO enrichParticipant(ActivityVO vo) {
        if (vo != null) {
            Long userId = SecurityUtil.getCurrentUserId();
            vo.setIsParticipant(participantService.isParticipant(vo.getId(), userId));
        }
        return vo;
    }

    @PostMapping("/create")
    @Operation(summary = "Create activity", description = "Create a new activity, creator becomes the owner")
    public Result<Void> createActivity(@Valid @RequestBody ActivityCreateCmd cmd) {
        Long userId = SecurityUtil.getCurrentUserId();
        activityService.createActivity(cmd, userId);
        return Result.success(null, "Activity created");
    }

    @GetMapping("/{activityId}")
    @Operation(summary = "Get activity by ID", description = "Get detailed information of an activity")
    public Result<ActivityVO> getActivity(
            @Parameter(description = "Activity ID") @PathVariable Long activityId) {
        return Result.success(enrichParticipant(activityService.getActivityById(activityId)));
    }

    @PatchMapping("/{activityId}")
    @Operation(summary = "Update activity", description = "Update activity fields (partial update)")
    public Result<Void> updateActivity(
            @Parameter(description = "Activity ID") @PathVariable Long activityId,
            @Valid @RequestBody ActivityUpdateCmd cmd) {
        activityService.updateActivity(activityId, cmd);
        return Result.success(null, "Activity updated");
    }

    @DeleteMapping("/{activityId}")
    @Operation(summary = "Delete activity", description = "Soft delete an activity (mark as DELETED)")
    public Result<Void> deleteActivity(
            @Parameter(description = "Activity ID") @PathVariable Long activityId) {
        activityService.deleteActivity(activityId);
        return Result.success(null, "Activity deleted");
    }

    @PostMapping("/search")
    @Operation(summary = "Search activities", description = "Search activities by time range, location, participant count, etc.")
    public Result<List<ActivityVO>> searchActivities(@RequestBody ActivitySearchQuery query) {
        List<ActivityVO> list = filterService.search(query);
        if (list != null) {
            list.forEach(this::enrichParticipant);
        }
        return Result.success(list);
    }

    @PostMapping("/{activityId}/participants")
    @Operation(summary = "Participate in activity", description = "Current user signs up for an activity")
    public Result<Void> participate(
            @Parameter(description = "Activity ID") @PathVariable Long activityId) {
        Long userId = SecurityUtil.getCurrentUserId();
        participantService.participate(activityId, userId);
        return Result.success(null, "Signed up successfully");
    }

    @DeleteMapping("/{activityId}/participants/me")
    @Operation(summary = "Quit activity", description = "Current user quits a previously signed-up activity")
    public Result<Void> quit(
            @Parameter(description = "Activity ID") @PathVariable Long activityId) {
        Long userId = SecurityUtil.getCurrentUserId();
        participantService.quit(activityId, userId);
        return Result.success(null, "Quit successfully");
    }
}
