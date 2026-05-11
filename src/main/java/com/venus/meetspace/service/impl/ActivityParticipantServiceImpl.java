package com.venus.meetspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.venus.meetspace.common.enums.ActivityStatus;
import com.venus.meetspace.common.enums.ParticipantRole;
import com.venus.meetspace.common.enums.ResultCode;
import com.venus.meetspace.common.exception.BusinessException;
import com.venus.meetspace.convert.ActivityConvert;
import com.venus.meetspace.model.entity.Activity;
import com.venus.meetspace.model.entity.ActivityParticipant;
import com.venus.meetspace.model.vo.ActivityVO;
import com.venus.meetspace.repository.ActivityMapper;
import com.venus.meetspace.repository.ActivityParticipantMapper;
import com.venus.meetspace.service.ActivityParticipantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ActivityParticipantServiceImpl extends ServiceImpl<ActivityParticipantMapper, ActivityParticipant>
        implements ActivityParticipantService {

    private final ActivityMapper activityMapper;
    private final ActivityConvert activityConvert;

    public ActivityParticipantServiceImpl(ActivityMapper activityMapper,
                                           ActivityConvert activityConvert) {
        this.activityMapper = activityMapper;
        this.activityConvert = activityConvert;
    }

    @Override
    public void participate(Long activityId, Long userId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Activity not found");
        }
        if (!activity.getStatus().equals(ActivityStatus.READY)) {
            throw new BusinessException(ResultCode.STATUS_ERROR, "Activity is not open for signup");
        }
        if (activity.getSignupDeadline().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.STATUS_ERROR, "Signup deadline has passed");
        }

        LambdaQueryWrapper<ActivityParticipant> query = new LambdaQueryWrapper<>();
        query.eq(ActivityParticipant::getActivityId, activityId)
             .eq(ActivityParticipant::getParticipantId, userId);
        if (this.getOne(query) != null) {
            throw new BusinessException(ResultCode.NO_SUCH_OBJECT, "Already signed up for this activity");
        }

        ActivityParticipant ap = new ActivityParticipant();
        ap.setParticipantId(userId);
        ap.setActivityId(activityId);
        ap.setRole(ParticipantRole.NORMAL);
        this.save(ap);

        log.info("Participant signed up: userId={}, activityId={}", userId, activityId);
    }

    @Override
    public void quit(Long activityId, Long userId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Activity not found");
        }
        if (!activity.getStatus().equals(ActivityStatus.READY) &&
                !activity.getStatus().equals(ActivityStatus.CLOSED)) {
            throw new BusinessException(ResultCode.STATUS_ERROR, "Activity has ended");
        }

        LambdaQueryWrapper<ActivityParticipant> query = new LambdaQueryWrapper<>();
        query.eq(ActivityParticipant::getActivityId, activityId)
             .eq(ActivityParticipant::getParticipantId, userId);
        this.remove(query);

        log.info("Participant quit: userId={}, activityId={}", userId, activityId);
    }

    @Override
    public List<ActivityVO> getParticipatedActivities(Long userId) {
        List<ActivityParticipant> records = this.getBaseMapper().findByParticipantId(userId);
        List<Long> activityIds = records.stream()
                .map(ActivityParticipant::getActivityId)
                .toList();
        if (activityIds.isEmpty()) {
            return List.of();
        }
        List<Activity> activities = activityMapper.findAllByIds(activityIds);
        return activityConvert.toVOList(activities);
    }

    @Override
    public List<ActivityVO> getSignedUpActivities(Long userId) {
        List<ActivityParticipant> records = this.getBaseMapper().findByParticipantId(userId);
        List<Long> activityIds = records.stream()
                .filter(ap -> ap.getRole() == ParticipantRole.NORMAL)
                .map(ActivityParticipant::getActivityId)
                .toList();
        if (activityIds.isEmpty()) {
            return List.of();
        }
        List<Activity> activities = activityMapper.findAllByIds(activityIds);
        return activityConvert.toVOList(activities);
    }

    @Override
    public List<ActivityVO> getCreatedActivities(Long userId) {
        List<ActivityParticipant> records = this.getBaseMapper().findByParticipantId(userId);
        List<Long> activityIds = records.stream()
                .filter(ap -> ap.getRole() == ParticipantRole.CREATOR)
                .map(ActivityParticipant::getActivityId)
                .toList();
        if (activityIds.isEmpty()) {
            return List.of();
        }
        List<Activity> activities = activityMapper.findAllByIds(activityIds);
        return activityConvert.toVOList(activities);
    }
}
