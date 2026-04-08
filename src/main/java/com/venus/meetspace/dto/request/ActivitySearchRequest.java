package com.venus.meetspace.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivitySearchRequest {
    private TimeRange timeRange; // 时间范围
    private GeoDistance geoDistance; // 地理范围
    private ParticipantRange participantRange; // 参与人数范围
}

@Data
class TimeRange {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String preset; // TODAY, THIS_WEEK, THIS_MONTH
}

@Data
class GeoDistance {
    private double latitude;
    private double longitude;
    private double radiusKm;
    private double preset; // 1km, 3km, 5km, 10km, 20km
}

@Data
class ParticipantRange {
    private int min;
    private int max;
}

