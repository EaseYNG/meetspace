package com.venus.meetspace.model.vo;

import com.venus.meetspace.common.enums.ActivityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Activity view object")
public class ActivityVO {

    @Schema(description = "Activity ID")
    private Long id;

    @Schema(description = "Activity title")
    private String title;

    @Schema(description = "Activity description")
    private String description;

    @Schema(description = "Activity address")
    private String address;

    @Schema(description = "Cover image URL")
    private String image;

    @Schema(description = "Start time")
    private LocalDateTime startTime;

    @Schema(description = "End time")
    private LocalDateTime endTime;

    @Schema(description = "Signup deadline")
    private LocalDateTime signupDeadline;

    @Schema(description = "Activity status", example = "READY")
    private ActivityStatus status;

    @Schema(description = "Minimum participants")
    private Integer minParticipants;

    @Schema(description = "Maximum participants")
    private Integer maxParticipants;

    @Schema(description = "Latitude")
    private Double latitude;

    @Schema(description = "Longitude")
    private Double longitude;

    @Schema(description = "Whether the current user is a participant")
    private Boolean isParticipant;
}
