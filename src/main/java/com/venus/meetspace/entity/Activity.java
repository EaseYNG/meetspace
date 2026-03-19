package com.venus.meetspace.entity;

import com.venus.meetspace.common.type.ActivityStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private long ownerId; // 创建者id

    @Column(nullable = false, name = "start_time")
    private LocalDateTime startTime;
    @Column(nullable = false, name = "end_time")
    private LocalDateTime endTime;
    @Column(nullable = false, name = "signup_deadline")
    private LocalDateTime signupDeadline; // 报名截止时间
    @Column(nullable = false)
    private String title;
    private String description;
    @Column(nullable = false)
    private String address;

    private ActivityStatus status;

    private String image;
    private double latitude;
    private double longitude;

}
