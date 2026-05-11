package com.venus.meetspace.service.impl;

import com.venus.meetspace.convert.ActivityConvert;
import com.venus.meetspace.model.entity.Activity;
import com.venus.meetspace.model.query.ActivitySearchQuery;
import com.venus.meetspace.model.vo.ActivityVO;
import com.venus.meetspace.repository.ActivityMapper;
import com.venus.meetspace.service.ActivityFilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ActivityFilterServiceImpl implements ActivityFilterService {

    private final ActivityMapper activityMapper;
    private final ActivityConvert activityConvert;

    public ActivityFilterServiceImpl(ActivityMapper activityMapper, ActivityConvert activityConvert) {
        this.activityMapper = activityMapper;
        this.activityConvert = activityConvert;
    }

    @Override
    public List<ActivityVO> search(ActivitySearchQuery query) {
        List<Activity> result = activityMapper.findByConditions(
                query.getStartTime(),
                query.getEndTime(),
                query.getLongitude(),
                query.getLatitude(),
                query.getRadiusKm(),
                query.getMinParticipants(),
                query.getMaxParticipants()
        );
        if (result == null || result.isEmpty()) {
            return Collections.emptyList();
        }
        return activityConvert.toVOList(result);
    }
}
