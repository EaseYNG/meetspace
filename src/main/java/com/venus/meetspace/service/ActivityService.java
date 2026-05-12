package com.venus.meetspace.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.venus.meetspace.model.cmd.ActivityCreateCmd;
import com.venus.meetspace.model.cmd.ActivityUpdateCmd;
import com.venus.meetspace.model.entity.Activity;
import com.venus.meetspace.model.vo.ActivityVO;

import java.util.List;

public interface ActivityService extends IService<Activity> {

    Long createActivity(ActivityCreateCmd cmd, Long ownerId);

    void updateActivity(Long activityId, ActivityUpdateCmd cmd);

    void deleteActivity(Long activityId);

    ActivityVO getActivityById(Long activityId);

    List<ActivityVO> getActivitiesByIds(List<Long> ids);

    List<ActivityVO> getReadyActivities();
}
