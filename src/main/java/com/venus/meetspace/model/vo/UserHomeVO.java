package com.venus.meetspace.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "User home page view object")
public class UserHomeVO {

    @Schema(description = "User profile")
    private UserProfileVO profile;

    @Schema(description = "Ongoing activities (the user participated in)")
    private List<ActivityVO> ongoingActivities;

    @Schema(description = "Recommended activities")
    private List<ActivityVO> recommendedActivities;
}
