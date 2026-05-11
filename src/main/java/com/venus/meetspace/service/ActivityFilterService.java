package com.venus.meetspace.service;

import com.venus.meetspace.model.query.ActivitySearchQuery;
import com.venus.meetspace.model.vo.ActivityVO;

import java.util.List;

public interface ActivityFilterService {

    List<ActivityVO> search(ActivitySearchQuery query);
}
