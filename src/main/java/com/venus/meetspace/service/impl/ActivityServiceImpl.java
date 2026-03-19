package com.venus.meetspace.service.impl;

import com.venus.meetspace.common.type.ActivityStatus;
import com.venus.meetspace.dto.request.ActivityRequest;
import com.venus.meetspace.dto.response.ActivityResponse;
import com.venus.meetspace.entity.Activity;
import com.venus.meetspace.exception.BusinessException;
import com.venus.meetspace.repository.ActivityRepository;
import com.venus.meetspace.service.ActivityService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final UserServiceImpl usi;

    public ActivityServiceImpl(ActivityRepository activityRepository, UserServiceImpl usi) {
        this.activityRepository = activityRepository;
        this.usi = usi;
    }

    @Override
    public void createActivity(ActivityRequest activityRequest, Long ownerId) {
        Activity activity = new Activity();
        LocalDateTime now = LocalDateTime.now();

        if(activityRequest.getStartTime().isBefore(now)) {
            throw new BusinessException(406, "起始时间早于现在！");
        }
        activity.setOwnerId(ownerId);
        activity.setTitle(activityRequest.getTitle());
        activity.setStartTime(activityRequest.getStartTime());
        activity.setEndTime(activityRequest.getEndTime());
        activity.setSignupDeadline(activityRequest.getSignupDeadline());
        activity.setAddress(activityRequest.getAddress());
        activity.setImage(activityRequest.getImage());
        activity.setDescription(activityRequest.getDescription());

        activity.setStatus(ActivityStatus.READY); // 确保活动处于就绪状态

        activityRepository.save(activity);
    }

    @Override
    public void updateActivity(long id, ActivityRequest activityRequest, Long ownerId) {
        Activity activity = activityRepository.findById(id);

        if(activity.getStatus() == ActivityStatus.CLOSED ||
                activity.getStatus() == ActivityStatus.DELETED) {
            throw new BusinessException(408, "活动不可编辑");
        }
        // 编辑逻辑


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
        Activity activity = new Activity();
        ActivityResponse ar = new ActivityResponse();
        try {
            activity = activityRepository.findById(id);
        } catch (Exception e) {
            throw new BusinessException(407, "未找到该活动！");
        }

        ar.setId(activity.getId());
        ar.setTitle(activity.getTitle());
        ar.setStartTime(activity.getStartTime());
        ar.setEndTime(activity.getEndTime());
        ar.setSignupDeadline(activity.getSignupDeadline());
        ar.setAddress(activity.getAddress());
        ar.setStatus(activity.getStatus());
        ar.setImage(activity.getImage());
        ar.setDescription(activity.getDescription());

        return ar;
    }

    @Override
    public List<ActivityResponse> getActivityByOwnerId(long id) {
        List<Activity> temp = activityRepository.findByOwnerId(id);
        if(temp == null) {
            throw new BusinessException(407, "获取活动列表失败！");
        }
        List<ActivityResponse> list = new ArrayList<>();
        for(Activity a : temp) {
            ActivityResponse ar = new ActivityResponse();
            ar.setId(a.getId());
            ar.setTitle(a.getTitle());
            ar.setStartTime(a.getStartTime());
            ar.setEndTime(a.getEndTime());
            ar.setSignupDeadline(a.getSignupDeadline());
            ar.setAddress(a.getAddress());
            ar.setStatus(a.getStatus());
            ar.setImage(a.getImage());
            ar.setDescription(a.getDescription());
            list.add(ar);
        }
        return list;
    }

    @Override
    public List<ActivityResponse> getActiveActivityByOwnerId(long id) {

        return List.of();
    }
}
