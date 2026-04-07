package com.venus.meetspace.service.impl;

import com.venus.meetspace.annotation.Log;
import com.venus.meetspace.common.type.ActivityStatus;
import com.venus.meetspace.dto.response.ActivityResponse;
import com.venus.meetspace.entity.Activity;
import com.venus.meetspace.entity.ActivityParticipant;
import com.venus.meetspace.exception.BusinessException;
import com.venus.meetspace.mapper.ActivityMapper;
import com.venus.meetspace.mapper.ActivityParticipantMapper;
import com.venus.meetspace.repository.ActivityParticipantRepository;
import com.venus.meetspace.repository.ActivityRepository;
import com.venus.meetspace.service.ActivityParticipantService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Log
public class ActivityParticipantServiceImpl implements ActivityParticipantService {
    private final ActivityRepository activityRepository;
    private final ActivityParticipantRepository activityParticipantRepository;
    private final ActivityMapper activityMapper;

    public ActivityParticipantServiceImpl(ActivityRepository activityRepository,
                                          ActivityParticipantRepository activityParticipantRepository,
                                           ActivityMapper activityMapper) {
        this.activityRepository = activityRepository;
        this.activityParticipantRepository = activityParticipantRepository;
        this.activityMapper = activityMapper;
    }

    @Override
    public void signup(Long activityId, Long userId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new BusinessException(407, "活动未找到！"));

        // 检测活动状态
        if(!activity.getStatus().equals(ActivityStatus.READY)) {
            throw new BusinessException(408, "活动状态不可报名！");
        }

        // 检测报名ddl
        if(activity.getSignupDeadline().isBefore(LocalDateTime.now())) {
            throw new BusinessException(408, "已过报名时间！");
        }

        // 检测是否报名过
        activityParticipantRepository.findByActivityIdAndParticipantId(activityId, userId)
                .ifPresent(activityParticipant -> {
                    throw new BusinessException(408, "已报名该活动！");}
                );

        ActivityParticipant ap = new ActivityParticipant();
        ap.setParticipantId(userId);
        ap.setActivityId(activityId);
        activityParticipantRepository.save(ap);
    }

    @Override
    public void quit(Long activityId, Long userId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new BusinessException(407, "活动未找到！"));
        if(!activity.getStatus().equals(ActivityStatus.READY) &&
                !activity.getStatus().equals(ActivityStatus.CLOSED)) {
            throw new BusinessException(408, "活动已结束！");
        }
        ActivityParticipant ap = new ActivityParticipant();
        ap.setParticipantId(userId);
        ap.setActivityId(activityId);
        activityParticipantRepository.deleteByIds(activityId, userId);
    }

    @Override
    public List<ActivityResponse> getParticipatedActivities(Long participantId) {
        List<Activity> activities = new ArrayList<>();
        List<ActivityParticipant> participants = activityParticipantRepository
                .findByParticipantId(participantId)
                .orElseThrow();
        for(ActivityParticipant ap : participants) {
            long activityId = ap.getActivityId();
            Activity temp = activityRepository.findById(activityId)
                    .orElseThrow(() -> new BusinessException(404, "活动未找到！"));
            activities.add(temp);
        }

        return activityMapper.toResponseList(activities);
    }
}
