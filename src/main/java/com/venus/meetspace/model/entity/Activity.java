package com.venus.meetspace.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.venus.meetspace.common.enums.ActivityStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("activity")
public class Activity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String description;
    private String address;
    private String image;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("signup_deadline")
    private LocalDateTime signupDeadline;

    private ActivityStatus status;

    @TableField("min_participants")
    private Integer minParticipants;

    @TableField("max_participants")
    private Integer maxParticipants;

    private Double latitude;
    private Double longitude;
}
