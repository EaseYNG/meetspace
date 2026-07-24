package com.venus.meetspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.venus.meetspace.common.enums.ResultCode;
import com.venus.meetspace.common.exception.BusinessException;
import com.venus.meetspace.convert.ActivityConvert;
import com.venus.meetspace.model.cmd.ActivityCreateCmd;
import com.venus.meetspace.model.cmd.ActivityUpdateCmd;
import com.venus.meetspace.model.entity.Activity;
import com.venus.meetspace.model.entity.ActivityParticipant;
import com.venus.meetspace.model.vo.ActivityVO;
import com.venus.meetspace.repository.ActivityMapper;
import com.venus.meetspace.repository.ActivityParticipantMapper;
import com.venus.meetspace.service.ActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    private final ActivityConvert activityConvert;
    private final ActivityParticipantMapper participantMapper;
    private final ActivityMapper activityMapper;

    public ActivityServiceImpl(ActivityConvert activityConvert,
                               ActivityParticipantMapper participantMapper, ActivityMapper activityMapper) {
        this.activityConvert = activityConvert;
        this.participantMapper = participantMapper;
        this.activityMapper = activityMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createActivity(ActivityCreateCmd cmd, Long ownerId) {
        if (cmd.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.VALUE_ERROR, "Start time must be in the future");
        }

        Activity activity = activityConvert.toEntity(cmd);
        this.save(activity);

        ActivityParticipant ap = new ActivityParticipant();
        ap.setActivityId(activity.getId());
        ap.setParticipantId(ownerId);
        participantMapper.insert(ap);

        log.info("Activity created: id={}, owner={}", activity.getId(), ownerId);
        return activity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivity(Long activityId, ActivityUpdateCmd cmd) {
        Activity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Activity not found");
        }
        if (activity.getStatus() == 4 ||
                activity.getStatus() == 3) {
            throw new BusinessException(ResultCode.STATUS_ERROR, "Activity cannot be edited");
        }

        activityConvert.update(activity, cmd);
        this.updateById(activity);
        log.info("Activity updated: id={}", activityId);
    }

    @Override
    public void deleteActivity(Long activityId) {
        Activity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Activity not found");
        }
        activity.setStatus(3);
        this.updateById(activity);
        log.info("Activity deleted: id={}", activityId);
    }

    @Override
    public ActivityVO getActivityById(Long activityId) {
        Activity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Activity not found");
        }
        return activityConvert.toVO(activity);
    }

    @Override
    public List<ActivityVO> getActivitiesByIds(List<Long> ids) {
        List<Activity> activities = activityMapper.findAllByIds(ids);
        return activityConvert.toVOList(activities);
    }

    @Override
    public List<ActivityVO> getReadyActivities() {
        LambdaQueryWrapper<Activity> query = new LambdaQueryWrapper<>();
        query.eq(Activity::getStatus, 0);
        List<Activity> activities = this.list(query);
        return activityConvert.toVOList(activities);
    }
}
