package com.venus.meetspace.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用于传入更新活动的请求体<br>
 * 所有字段可为空
 */
@Data
public class ActivityUpdateRequest {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime signupDeadline;
    private String address;
    private String image;
    private String description;
}
