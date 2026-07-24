package com.venus.meetspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.venus.meetspace.common.enums.ResultCode;
import com.venus.meetspace.common.exception.BusinessException;
import com.venus.meetspace.convert.ActivityConvert;
import com.venus.meetspace.model.entity.Activity;
import com.venus.meetspace.model.entity.ActivityParticipant;
import com.venus.meetspace.model.enums.ActivityStatus;
import com.venus.meetspace.model.vo.ActivityVO;
import com.venus.meetspace.repository.ActivityMapper;
import com.venus.meetspace.repository.ActivityParticipantMapper;
import com.venus.meetspace.service.ActivityParticipantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ActivityParticipantServiceImpl extends ServiceImpl<ActivityParticipantMapper, ActivityParticipant>
        implements ActivityParticipantService {

    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private ActivityParticipantMapper activityParticipantMapper;
    @Autowired
    private ActivityConvert activityConvert;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void participate(Long activityId, Long userId) {
        Activity activity = activityMapper.selectById(activityId);

        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Activity not found");
        }
        if (activity.getStatus() != ActivityStatus.READY) {
            throw new BusinessException(ResultCode.STATUS_ERROR, "Activity is not open for signup");
        }
        if (activity.getSignupDeadline().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.STATUS_ERROR, "Signup deadline has passed");
        }

        ActivityParticipant ap = activityParticipantMapper.findBy2Ids(activityId, userId);
        if(ap != null) {
            throw new BusinessException(ResultCode.NO_SUCH_OBJECT, "Already signed up for this activity");
        }

        // 检查最大参与人数
        if (activity.getMaxParticipants() != null) {
            LambdaQueryWrapper<ActivityParticipant> countQuery = new LambdaQueryWrapper<>();
            countQuery.eq(ActivityParticipant::getActivityId, activityId);
            long currentCount = this.count(countQuery);
            if (currentCount >= activity.getMaxParticipants()) {
                throw new BusinessException(ResultCode.STATUS_ERROR, "Activity has reached the maximum number of participants");
            }
        }

        ap = new ActivityParticipant();
        ap.setParticipantId(userId);
        ap.setActivityId(activityId);
        this.save(ap);

        log.info("Participant signed up: userId={}, activityId={}", userId, activityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void quit(Long activityId, Long userId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Activity not found");
        }
        if (activity.getStatus() != ActivityStatus.READY &&
                activity.getStatus() != ActivityStatus.CLOSED) {
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
            return new ArrayList<>();
        }
        List<Activity> activities = activityMapper.findAllByIds(activityIds);
        return new ArrayList<>(activityConvert.toVOList(activities));
    }

    @Override
    public List<ActivityVO> getSignedUpActivities(Long userId) {
        List<ActivityParticipant> records = this.getBaseMapper().findByParticipantId(userId);
        List<Long> activityIds = records.stream()
                .map(ActivityParticipant::getActivityId)
                .toList();
        if (activityIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Activity> activities = activityMapper.findAllByIds(activityIds);
        return new ArrayList<>(activityConvert.toVOList(activities));
    }

    @Override
    public boolean isParticipant(Long activityId, Long userId) {
        LambdaQueryWrapper<ActivityParticipant> query = new LambdaQueryWrapper<>();
        query.eq(ActivityParticipant::getActivityId, activityId)
             .eq(ActivityParticipant::getParticipantId, userId);
        return this.count(query) > 0;
    }

    @Override
    public List<ActivityVO> getCreatedActivities(Long userId) {
        List<Activity> activities = activityMapper.findByOwnerId(userId);
        return activityConvert.toVOList(activities);
    }
}
