package com.venus.meetspace.model.query;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivitySearchQuery {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double longitude;
    private Double latitude;
    private Double radiusKm;
    private Integer minParticipants;
    private Integer maxParticipants;
}
