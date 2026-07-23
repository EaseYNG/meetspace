package com.venus.meetspace.model.cmd;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityUpdateCmd {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime signupDeadline;
    private String address;
    private String image;
    private String description;
}
