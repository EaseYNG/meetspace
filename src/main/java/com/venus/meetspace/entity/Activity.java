package com.venus.meetspace.entity;

import com.venus.meetspace.common.type.ActivityStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private long ownerId; // 创建者id
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;
    @Column(nullable = false)
    private String title;
    private String description;
    private String image;
    @Column(nullable = false)
    private LocalDateTime signupDeadline; // 报名截止时间
    private ActivityStatus status;
    @Column(nullable = false)
    private String address;
    private double latitude;
    private double longitude;

}
