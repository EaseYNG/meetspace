package com.venus.meetspace.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.venus.meetspace.model.entity.ActivityParticipant;
import com.venus.meetspace.model.vo.ActivityVO;

import java.util.List;

public interface ActivityParticipantService extends IService<ActivityParticipant> {

    void participate(Long activityId, Long userId);

    void quit(Long activityId, Long userId);

    List<ActivityVO> getParticipatedActivities(Long userId);

    List<ActivityVO> getCreatedActivities(Long userId);

    List<ActivityVO> getSignedUpActivities(Long userId);
}
