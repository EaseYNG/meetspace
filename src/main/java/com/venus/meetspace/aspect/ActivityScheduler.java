package com.venus.meetspace.aspect;

import com.venus.meetspace.cache.CacheService;
import com.venus.meetspace.common.enums.ActivityStatus;
import com.venus.meetspace.model.entity.Activity;
import com.venus.meetspace.repository.ActivityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class ActivityScheduler {

    private final ActivityMapper activityMapper;
    private final CacheService cacheService;

    public ActivityScheduler(ActivityMapper activityMapper, CacheService cacheService) {
        this.activityMapper = activityMapper;
        this.cacheService = cacheService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void updateActivityStatus() {
        List<Activity> readyActivities = activityMapper.findAllReady();
        boolean anyClosed = false;
        for (Activity activity : readyActivities) {
            if (activity.getSignupDeadline() != null
                    && activity.getSignupDeadline().isBefore(LocalDateTime.now())) {
                activity.setStatus(ActivityStatus.CLOSED);
                activityMapper.updateById(activity);
                cacheService.delete("activity:" + activity.getId());
                anyClosed = true;
                log.info("已更改活动状态: {} -> CLOSED", activity.getId());
            }
        }
        if (anyClosed) {
            cacheService.delete("activity:ready:list");
        }
    }
}
