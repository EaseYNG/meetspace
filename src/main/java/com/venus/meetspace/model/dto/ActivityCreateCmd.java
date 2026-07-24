package com.venus.meetspace.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityCreateCmd {

    @NotBlank(message = "活动标题不能为空")
    private String title;

    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须在未来")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @NotNull(message = "报名截止时间不能为空")
    private LocalDateTime signupDeadline;

    private String address;
    private Integer minParticipants;
    private Integer maxParticipants;
    private String image;
    private String description;
}
