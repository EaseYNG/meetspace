package com.venus.meetspace.aspect;

import com.venus.meetspace.common.type.ActivityStatus;
import com.venus.meetspace.entity.Activity;
import com.venus.meetspace.repository.ActivityRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class ActivityScheduler {
    private final ActivityRepository activityRepo;

    public ActivityScheduler(ActivityRepository activityRepo) {
        this.activityRepo = activityRepo;
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void updateActivityStatus() {
        for(Activity activity : activityRepo.findAllReady()) {
            if(activity.getSignupDeadline().isBefore(LocalDateTime.now())) {
                activity.setStatus(ActivityStatus.CLOSED);
                activityRepo.save(activity);
            }
        }
    }
}
