package com.venus.meetspace.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivitySearchRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double longitude; // 经度
    private double latitude; // 纬度
    private double radiusKm;
    private int min;
    private int max; // 参与人数范围
}