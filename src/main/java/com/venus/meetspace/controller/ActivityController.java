package com.venus.meetspace.controller;

import com.venus.meetspace.annotation.CurrentUserId;
import com.venus.meetspace.annotation.Log;
import com.venus.meetspace.common.Result;
import com.venus.meetspace.dto.request.ActivityRequest;
import com.venus.meetspace.dto.response.ActivityResponse;
import com.venus.meetspace.service.impl.ActivityServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity")
@Log
public class ActivityController {
    private final ActivityServiceImpl asi;

    public ActivityController(ActivityServiceImpl asi) {
        this.asi = asi;
    }

    @PostMapping("/create")
    public Result<Void> createActivity(@RequestBody ActivityRequest ar,
                                       @CurrentUserId Long userId) {
        asi.createActivity(ar, userId);

        return Result.success(null, "活动创建成功！");
    }

    @PutMapping("/update")
    public Result<Void> updateActivity(@RequestParam Long activityId,
                                       @RequestBody ActivityRequest ar,
                                       @CurrentUserId Long userId) {
        asi.updateActivity(activityId, ar, userId);

        return Result.success(null, "活动更新成功！");
    }

    @GetMapping("/list")
    public Result<List<ActivityResponse>> list(@CurrentUserId Long userId) {
        List<ActivityResponse> list = asi.getActivityByOwnerId(userId);

        return Result.success(list, "获取活动列表成功！");
    }

}
