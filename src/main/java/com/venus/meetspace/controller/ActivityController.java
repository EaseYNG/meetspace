package com.venus.meetspace.controller;

import com.venus.meetspace.annotation.CurrentUserId;
import com.venus.meetspace.common.Result;
import com.venus.meetspace.dto.request.ActivityCreateRequest;
import com.venus.meetspace.dto.request.ActivitySearchRequest;
import com.venus.meetspace.dto.request.ActivityUpdateRequest;
import com.venus.meetspace.dto.response.ActivityResponse;
import com.venus.meetspace.service.impl.ActivityFilterImpl;
import com.venus.meetspace.service.impl.ActivityParticipantServiceImpl;
import com.venus.meetspace.service.impl.ActivityServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity")
@Slf4j
public class ActivityController {
    private final ActivityServiceImpl asi;
    private final ActivityParticipantServiceImpl apsi;
    private final ActivityFilterImpl afi;

    public ActivityController(ActivityServiceImpl asi,
                              ActivityParticipantServiceImpl apsi, ActivityFilterImpl afi) {
        this.asi = asi;
        this.apsi = apsi;
        this.afi = afi;
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

    @GetMapping("/related")
    public Result<List<ActivityResponse>> getParticipatedActivities(@CurrentUserId Long participantId) {
        return Result.success(apsi.getRelatedActivities(participantId), "用户参加活动列表获取成功！");
    }

    @GetMapping("/created")
    public Result<List<ActivityResponse>> getCreatedActivities(@CurrentUserId Long userId) {
        return Result.success(apsi.getCreatedActivities(userId));
    }

    @GetMapping("/signed_up")
    public Result<List<ActivityResponse>> getSignedUpActivities(@CurrentUserId Long userId) {
        return Result.success(apsi.getSignedUpActivities(userId));
    }

    @PostMapping("/search")
    public Result<List<ActivityResponse>> searchActivities(@RequestBody ActivitySearchRequest ar) {
        return Result.success(afi.search(ar));
    }

    @GetMapping("/quit/{activityId}")
    public Result<Void> quit(@PathVariable Long activityId,
                             @CurrentUserId Long userId) {
        apsi.quit(activityId, userId);
        return Result.success(null);
    }
}
