package com.venus.meetspace.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserHomeVO {
    private UserProfileVO profile;
    private List<ActivityVO> ongoingActivities;
    private List<ActivityVO> recommendedActivities;
}
