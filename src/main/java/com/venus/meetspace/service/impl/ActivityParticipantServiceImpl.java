package com.venus.meetspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.venus.meetspace.cache.CacheService;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ActivityParticipantServiceImpl extends ServiceImpl<ActivityParticipantMapper, ActivityParticipant>
        implements ActivityParticipantService {

    private final ActivityMapper activityMapper;
    private final ActivityParticipantMapper activityParticipantMapper;
    private final ActivityConvert activityConvert;
    private final CacheService cacheService;

    public ActivityParticipantServiceImpl(ActivityMapper activityMapper, ActivityParticipantMapper activityParticipantMapper,
                                          ActivityConvert activityConvert, CacheService cacheService) {
        this.activityMapper = activityMapper;
        this.activityParticipantMapper = activityParticipantMapper;
        this.activityConvert = activityConvert;
        this.cacheService = cacheService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void participate(Long activityId, Long userId) {
        Activity activity = cacheService.getOrLoad(
                "activity:id:" + activityId,
                Activity.class,
                30,
                TimeUnit.MINUTES,
                () -> {
                    Activity temp = activityMapper.selectById(activityId);
                    return temp;
                }
        );

        // 状态校验
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Activity not found");
        }
        if (!activity.getStatus().equals(ActivityStatus.READY)) {
            throw new BusinessException(ResultCode.STATUS_ERROR, "Activity is not open for signup");
        }
        if (activity.getSignupDeadline().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.STATUS_ERROR, "Signup deadline has passed");
        }

        // 查参加记录
        ActivityParticipant ap = cacheService.getOrLoad(
            "activity_participant:activity_id:" + activityId + ":participant_id:" + userId,
            ActivityParticipant.class,
                30,
                TimeUnit.MINUTES,
                () -> {
                    ActivityParticipant temp = activityParticipantMapper.findBy2Ids(activityId, userId);
                    return temp;
                }
        );
        if(ap != null) {
            throw new BusinessException(ResultCode.NO_SUCH_OBJECT, "Already signed up for this activity");
        }
        ap = new ActivityParticipant();
        ap.setParticipantId(userId);
        ap.setActivityId(activityId);
        ap.setRole(ParticipantRole.NORMAL);
        this.save(ap);

        cacheService.delete("user:participated:" + userId);
        cacheService.delete("user:signed_up:" + userId);
        log.info("Participant signed up: userId={}, activityId={}", userId, activityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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

        cacheService.delete("activity_participant:activity_id:" + activityId + ":participant_id:" + userId);
        cacheService.delete("user:participated:" + userId);
        cacheService.delete("user:signed_up:" + userId);
        log.info("Participant quit: userId={}, activityId={}", userId, activityId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ActivityVO> getParticipatedActivities(Long userId) {
        return cacheService.getOrLoad("user:participated:" + userId, List.class, 5, TimeUnit.MINUTES, () -> {
            List<ActivityParticipant> records = this.getBaseMapper().findByParticipantId(userId);
            List<Long> activityIds = records.stream()
                    .map(ActivityParticipant::getActivityId)
                    .toList();
            if (activityIds.isEmpty()) {
                return new java.util.ArrayList<>();
            }
            List<Activity> activities = activityMapper.findAllByIds(activityIds);
            return new java.util.ArrayList<>(activityConvert.toVOList(activities));
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ActivityVO> getSignedUpActivities(Long userId) {
        return cacheService.getOrLoad("user:signed_up:" + userId, List.class, 5, TimeUnit.MINUTES, () -> {
            List<ActivityParticipant> records = this.getBaseMapper().findByParticipantId(userId);
            List<Long> activityIds = records.stream()
                    .filter(ap -> ap.getRole() == ParticipantRole.NORMAL)
                    .map(ActivityParticipant::getActivityId)
                    .toList();
            if (activityIds.isEmpty()) {
                return new java.util.ArrayList<>();
            }
            List<Activity> activities = activityMapper.findAllByIds(activityIds);
            return new java.util.ArrayList<>(activityConvert.toVOList(activities));
        });
    }

    @Override
    public boolean isParticipant(Long activityId, Long userId) {
        LambdaQueryWrapper<ActivityParticipant> query = new LambdaQueryWrapper<>();
        query.eq(ActivityParticipant::getActivityId, activityId)
             .eq(ActivityParticipant::getParticipantId, userId);
        return this.count(query) > 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ActivityVO> getCreatedActivities(Long userId) {
        return cacheService.getOrLoad("user:created:" + userId, List.class, 5, TimeUnit.MINUTES, () -> {
            List<ActivityParticipant> records = this.getBaseMapper().findByParticipantId(userId);
            List<Long> activityIds = records.stream()
                    .filter(ap -> ap.getRole() == ParticipantRole.CREATOR)
                    .map(ActivityParticipant::getActivityId)
                    .toList();
            if (activityIds.isEmpty()) {
                return new java.util.ArrayList<>();
            }
            List<Activity> activities = activityMapper.findAllByIds(activityIds);
            return new java.util.ArrayList<>(activityConvert.toVOList(activities));
        });
    }
}
