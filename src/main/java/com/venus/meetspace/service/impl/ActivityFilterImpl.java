package com.venus.meetspace.service.impl;

import com.venus.meetspace.dto.request.ActivitySearchRequest;
import com.venus.meetspace.dto.response.ActivityResponse;
import com.venus.meetspace.entity.Activity;
import com.venus.meetspace.mapper.ActivityMapper;
import com.venus.meetspace.repository.ActivityRepository;
import com.venus.meetspace.service.ActivityFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ActivityFilterImpl implements ActivityFilter {
    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;

    public ActivityFilterImpl(ActivityRepository activityRepository, ActivityMapper activityMapper) {
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
    }

    /**
     * 将request转换为查询条件字段，执行preset计算
     * 调用repo的findByConditions(
     *      LocalDateTime startTime,
     *      LocalDateTime endTime,
     *      double longitude,
     *      double latitude,
     *      double radius,
     *      Integer min,
     *      Integer max
     * )
     * @param r 筛选请求
     * @return 符合条件的活动列表
     */
    @Override
    public List<ActivityResponse> search(ActivitySearchRequest r) {
        List<Activity> temp = activityRepository.findByConditions(
                r.getStartTime(),
                r.getEndTime(),
                r.getLongitude(),
                r.getLatitude(),
                r.getRadiusKm(),
                r.getMin(),
                r.getMax()
        ).orElseThrow();
        return activityMapper.toResponseList(temp);
    }
}
