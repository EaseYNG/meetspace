package com.venus.meetspace.service.impl;

import com.venus.meetspace.dto.request.ActivitySearchRequest;
import com.venus.meetspace.dto.response.ActivityResponse;
import com.venus.meetspace.mapper.ActivityMapper;
import com.venus.meetspace.repository.ActivityRepository;
import com.venus.meetspace.service.ActivityFilter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityFilterImpl implements ActivityFilter {
    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;

    public ActivityFilterImpl(ActivityRepository activityRepository, ActivityMapper activityMapper) {
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
    }

    @Override
    public List<ActivityResponse> search(ActivitySearchRequest request) {

        return List.of();
    }
}
