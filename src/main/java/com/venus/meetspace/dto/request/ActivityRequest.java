package com.venus.meetspace.dto.request;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
public class ActivityRequest {
    @NonNull
    private String title;
    @NonNull
    private LocalDateTime startTime;
    @NonNull
    private LocalDateTime endTime;
    @NonNull
    private LocalDateTime signupDeadline;
    @NonNull
    private String address;

    private String image;
    private String description;

}
