package com.venus.meetspace.service;

import com.venus.meetspace.dto.request.ActivityCreateRequest;
import com.venus.meetspace.dto.request.ActivityUpdateRequest;
import com.venus.meetspace.dto.response.ActivityResponse;

import java.util.List;

public interface ActivityService {
    void createActivity(ActivityCreateRequest activityRequest, Long ownerId);
    void updateActivity(Long id, ActivityUpdateRequest activityUpdateRequest, Long ownerId);
    void deleteActivity(Long id);

    /**
     * 根据活动id获取活动vo
     * @param id 活动id
     * @return 活动id对应的活动
     */
    ActivityResponse getActivityById(Long id);

    /**
     * 获取所有活动
     * @param id 所有者id
     * @return 所有者的全部活动
     */
    List<ActivityResponse> getActivityByOwnerId(Long id);

    /**
     * 获取所有活动状态下的活动
     * @param id 所有者id
     * @return 所有者的全部活动状态下的活动
     */
    List<ActivityResponse> getActiveActivityByOwnerId(Long id);
}
