package com.venus.meetspace.controller;

import com.venus.meetspace.annotation.CurrentUserId;
import com.venus.meetspace.annotation.Log;
import com.venus.meetspace.common.Result;
import com.venus.meetspace.dto.request.ActivityCreateRequest;
import com.venus.meetspace.dto.request.ActivityUpdateRequest;
import com.venus.meetspace.dto.response.ActivityResponse;
import com.venus.meetspace.service.impl.ActivityParticipantServiceImpl;
import com.venus.meetspace.service.impl.ActivityServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity")
@Log
public class ActivityController {
    private final ActivityServiceImpl asi;
    private final ActivityParticipantServiceImpl apsi;

    public ActivityController(ActivityServiceImpl asi,
                              ActivityParticipantServiceImpl apsi) {
        this.asi = asi;
        this.apsi = apsi;
    }

    @PostMapping("/create")
    public Result<Void> createActivity(@RequestBody ActivityCreateRequest ar,
                                       @CurrentUserId Long userId) {
        asi.createActivity(ar, userId);
        return Result.success(null, "活动创建成功！");
    }

    @PatchMapping("/update/{activityId}")
    public Result<Void> updateActivity(@PathVariable Long activityId,
                                       @RequestBody ActivityUpdateRequest ar,
                                       @CurrentUserId Long userId) {
        asi.updateActivity(activityId, ar, userId);
        return Result.success(null, "活动更新成功！");
    }

    @DeleteMapping("/delete/{activityId}")
    public Result<Void> deleteActivity(@PathVariable Long activityId) {
        asi.deleteActivity(activityId);
        return Result.success(null, "活动删除成功！");
    }

    @GetMapping("/signup/{activityId}")
    public Result<Void> signupActivity(@PathVariable Long activityId,
                                       @CurrentUserId Long userId) {
        apsi.signup(activityId, userId);
        return Result.success(null, "活动报名成功！");
    }

    @GetMapping("/participated")
    public Result<List<ActivityResponse>> getParticipatedActivities(@CurrentUserId Long participantId) {
        return Result.success(apsi.getParticipatedActivities(participantId), "用户参加活动列表获取成功！");
    }

    @GetMapping("/created")
    public Result<List<ActivityResponse>> getCreatedActivities(@CurrentUserId Long userId) {
        return Result.success(asi.getActivityByOwnerId(userId));
    }

    /**
     * 仅用于测试，获取所有活动
     */
    @GetMapping("/list")
    public Result<List<ActivityResponse>> list(@CurrentUserId Long userId) {
        List<ActivityResponse> list = asi.getActivityByOwnerId(userId);
        return Result.success(list, "获取活动列表成功！");
    }

}
