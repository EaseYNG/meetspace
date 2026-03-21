package com.venus.meetspace.service.impl;

import com.venus.meetspace.common.type.ActivityStatus;
import com.venus.meetspace.dto.request.ActivityCreateRequest;
import com.venus.meetspace.dto.request.ActivityUpdateRequest;
import com.venus.meetspace.dto.response.ActivityResponse;
import com.venus.meetspace.entity.Activity;
import com.venus.meetspace.exception.BusinessException;
import com.venus.meetspace.mapper.ActivityMapper;
import com.venus.meetspace.repository.ActivityRepository;
import com.venus.meetspace.service.ActivityService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final UserServiceImpl usi;
    private final ActivityMapper activityMapper;

    public ActivityServiceImpl(ActivityRepository activityRepository, UserServiceImpl usi, ActivityMapper activityMapper) {
        this.activityRepository = activityRepository;
        this.usi = usi;
        this.activityMapper = activityMapper;
    }

    @Override
    public void createActivity(ActivityCreateRequest activityRequest, Long ownerId) {
        LocalDateTime now = LocalDateTime.now();

        if(activityRequest.getStartTime().isBefore(now)) {
            throw new BusinessException(406, "起始时间早于现在！");
        }
        Activity activity = activityMapper.toEntity(activityRequest);
        activity.setOwnerId(ownerId);
        activity.setStatus(ActivityStatus.READY); // 确保活动处于就绪状态

        activityRepository.save(activity);
    }

    @Override
    public void updateActivity(long id, ActivityUpdateRequest activityUpdateRequest, Long ownerId) {
        Activity activity = activityRepository.findById(id);

        if(activity.getStatus() == ActivityStatus.CLOSED ||
                activity.getStatus() == ActivityStatus.DELETED) {
            throw new BusinessException(408, "活动不可编辑");
        }
        // 编辑逻辑
        activityMapper.update(activityUpdateRequest, activity);
        activityRepository.save(activity);
    }


    @Override
    public void deleteActivity(long id) {
        Activity activity = activityRepository.findById(id);
        activity.setStatus(ActivityStatus.DELETED); // 设置活动状态

        activityRepository.save(activity);
    }

    @Override
    public ActivityResponse getActivityById(long id) {
        Activity activity;
        try {
            activity = activityRepository.findById(id);
        } catch (Exception e) {
            throw new BusinessException(407, "未找到该活动！");
        }

        ActivityResponse ar = activityMapper.toResponse(activity);
        return ar;
    }

    @Override
    public List<ActivityResponse> getActivityByOwnerId(long id) {
        List<Activity> temp = activityRepository.findByOwnerId(id);
        if(temp == null) {
            throw new BusinessException(407, "获取活动列表失败！");
        }
        return activityMapper.toResponseList(temp);
    }

    @Override
    public List<ActivityResponse> getActiveActivityByOwnerId(long id) {

        return List.of();
    }
}
