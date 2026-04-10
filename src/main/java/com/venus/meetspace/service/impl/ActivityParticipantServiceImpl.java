package com.venus.meetspace.service.impl;

import com.venus.meetspace.common.type.ActivityStatus;
import com.venus.meetspace.common.type.Role;
import com.venus.meetspace.dto.response.ActivityResponse;
import com.venus.meetspace.entity.Activity;
import com.venus.meetspace.entity.ActivityParticipant;
import com.venus.meetspace.exception.BusinessException;
import com.venus.meetspace.mapper.ActivityMapper;
import com.venus.meetspace.repository.ActivityParticipantRepository;
import com.venus.meetspace.repository.ActivityRepository;
import com.venus.meetspace.service.ActivityParticipantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
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
        ap.setRole(Role.NORMAL);
        activityParticipantRepository.save(ap);
        log.info("报名活动: " + "user_id: " + userId + "activity_id: " + activityId);
    }

    @Override
    public void quit(Long activityId, Long userId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new BusinessException(407, "活动未找到！"));
        if(!activity.getStatus().equals(ActivityStatus.READY) &&
                !activity.getStatus().equals(ActivityStatus.CLOSED)) {
            throw new BusinessException(408, "活动已结束！");
        }
        activityParticipantRepository.deleteByIds(activityId, userId);
        log.info("退出活动: " + "user_id: " + userId + "activity_id: " + activityId);
    }

    /**
     * 工具方法：获取用户有参与记录的全部活动列表
     * 用于获取原始ActivityParticipant对象进行role校验
     * @param participantId 参与者ID
     * @return 用户有参与记录的全部活动列表
     */
    private List<ActivityParticipant> _getRelatedActivities(Long participantId) {
        return activityParticipantRepository.findByParticipantId(participantId).orElseThrow();
    }

    @Override
    public List<ActivityResponse> getRelatedActivities(Long participantId) {
        List<ActivityParticipant> temp = _getRelatedActivities(participantId);
        List<Long> tempIds = new ArrayList<>();
        for(ActivityParticipant ap : temp) {
            tempIds.add(ap.getId());
        }
        List<Activity> activities = activityRepository.findAllByIds(tempIds);
        return activityMapper.toResponseList(activities);
    }

    @Override
    public List<ActivityResponse> getSignedUpActivities(Long participantId) {
        List<ActivityParticipant> temp = _getRelatedActivities(participantId);
        List<Long> tempIds = new ArrayList<>();
        for(ActivityParticipant ap : temp) {
            if(ap.getRole() == Role.NORMAL) {
                tempIds.add(ap.getId());
            }
        }
        List<Activity> activities = activityRepository.findAllByIds(tempIds);
        return activityMapper.toResponseList(activities);
    }

    @Override
    public List<ActivityResponse> getCreatedActivities(Long participantId) {
        List<ActivityParticipant> temp = _getRelatedActivities(participantId);
        List<Long> tempIds = new ArrayList<>();
        for(ActivityParticipant ap : temp) {
            if(ap.getRole() == Role.CREATOR) {
                tempIds.add(ap.getId());
            }
        }
        List<Activity> activities = activityRepository.findAllByIds(tempIds);
        return activityMapper.toResponseList(activities);
    }
}
