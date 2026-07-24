package com.venus.meetspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.venus.meetspace.common.enums.ResultCode;
import com.venus.meetspace.common.exception.BusinessException;
import com.venus.meetspace.convert.ActivityConvert;
import com.venus.meetspace.model.dto.ActivityCreateCmd;
import com.venus.meetspace.model.dto.ActivityUpdateCmd;
import com.venus.meetspace.model.entity.Activity;
import com.venus.meetspace.model.enums.ActivityStatus;
import com.venus.meetspace.model.vo.ActivityVO;
import com.venus.meetspace.security.SecurityUtil;
import com.venus.meetspace.repository.ActivityMapper;
import com.venus.meetspace.service.ActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {
    @Autowired
    private ActivityConvert activityConvert;
    @Autowired
    private ActivityMapper activityMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createActivity(ActivityCreateCmd cmd, Long ownerId) {
        if (cmd.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.VALUE_ERROR, "Start time must be in the future");
        }

        Activity activity = activityConvert.toEntity(cmd);
        activity.setOwnerId(ownerId);
        this.save(activity);

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
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (!activity.getOwnerId().equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "Only the owner can edit this activity");
        }
        if (activity.getStatus() == ActivityStatus.DELETED ||
                activity.getStatus() == ActivityStatus.OVER) {
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
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (!activity.getOwnerId().equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "Only the owner can delete this activity");
        }
        activity.setStatus(ActivityStatus.DELETED);
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
        query.eq(Activity::getStatus, ActivityStatus.READY);
        List<Activity> activities = this.list(query);
        return activityConvert.toVOList(activities);
    }
}
