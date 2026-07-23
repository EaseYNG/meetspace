package com.venus.meetspace.model.cmd;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityCreateCmd {

    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime signupDeadline;
    private String address;
    private Integer minParticipants;
    private Integer maxParticipants;
    private String image;
    private String description;
}
