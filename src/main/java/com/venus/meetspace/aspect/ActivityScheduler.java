package com.venus.meetspace.aspect;

import com.venus.meetspace.common.enums.ActivityStatus;
import com.venus.meetspace.model.entity.Activity;
import com.venus.meetspace.repository.ActivityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class ActivityScheduler {
    @Autowired
    private ActivityMapper activityMapper;

    @Scheduled(cron = "0 * * * * *")
    public void updateActivityStatus() {
        List<Activity> readyActivities = activityMapper.findAllReady();
        boolean anyClosed = false;
        for (Activity activity : readyActivities) {
            if (activity.getSignupDeadline() != null
                    && activity.getSignupDeadline().isBefore(LocalDateTime.now())) {
                activity.setStatus(ActivityStatus.CLOSED);
                activityMapper.updateById(activity);
                anyClosed = true;
                log.info("已更改活动状态: {} -> CLOSED", activity.getId());
            }
        }
    }
}
