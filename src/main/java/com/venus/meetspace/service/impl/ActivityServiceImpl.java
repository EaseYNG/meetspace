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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;

    public ActivityServiceImpl(ActivityRepository activityRepository, ActivityMapper activityMapper) {
        this.activityRepository = activityRepository;
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
        log.info("活动创建: " + activity.getId());
    }

    @Override
    public void updateActivity(Long id, ActivityUpdateRequest activityUpdateRequest, Long ownerId) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(407, "未找到该活动！"));
        if(activity.getOwnerId() != ownerId) {
            throw new BusinessException(408, "活动不可编辑");
        }
        if(activity.getStatus() == ActivityStatus.CLOSED ||
                activity.getStatus() == ActivityStatus.DELETED) {
            throw new BusinessException(408, "活动不可编辑");
        }
        // 编辑逻辑
        activityMapper.update(activityUpdateRequest, activity);
        activityRepository.save(activity);
        log.info("活动更新: " + activity.getId());
    }


    @Override
    public void deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(407, "未找到该活动！"));
        activity.setStatus(ActivityStatus.DELETED); // 设置活动状态
        activityRepository.save(activity);
        log.info("活动删除: " + activity.getId());
    }

    @Override
    public ActivityResponse getActivityById(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(407, "未找到该活动！"));
        log.info("获取活动: " + activity.getId());
        return activityMapper.toResponse(activity);
    }

    @Override
    public List<ActivityResponse> getActivityByOwnerId(Long id) {
        List<Activity> temp = activityRepository.findByOwnerId(id)
                .orElseThrow();
        log.info("获取活动列表: ");
        for(Activity a : temp) {
            log.info(String.valueOf(a.getId())
                    + " "
                    + a.getMinParticipants()
                    + " "
                    + a.getMaxParticipants());
        }
        return activityMapper.toResponseList(temp);
    }

    @Override
    public List<ActivityResponse> getActiveActivityByOwnerId(Long id) {

        return List.of();
    }
}
