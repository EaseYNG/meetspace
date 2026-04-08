package com.venus.meetspace.dto.response;

import com.venus.meetspace.common.type.ActivityStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityResponse {

    private long id;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime signupDeadline;
    private String address;
    private ActivityStatus status;
    private int minParticipants;
    private int maxParticipants;

    private String image;
    private String description;

}
