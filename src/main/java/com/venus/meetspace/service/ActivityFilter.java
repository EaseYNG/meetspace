package com.venus.meetspace.service;

import com.venus.meetspace.dto.request.ActivitySearchRequest;
import com.venus.meetspace.dto.response.ActivityResponse;

import java.util.List;


public interface ActivityFilter {
    /**
     * 根据传入条件筛选活动
     */
    List<ActivityResponse> search(ActivitySearchRequest request);
}
