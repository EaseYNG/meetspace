package com.venus.meetspace.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityVO {
    private Long id;
    private String title;
    private String description;
    private String address;
    private String image;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime signupDeadline;
    private Integer status;
    private Integer minParticipants;
    private Integer maxParticipants;
    private Double latitude;
    private Double longitude;
    private Boolean isParticipant;
}
