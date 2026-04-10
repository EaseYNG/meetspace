package com.venus.meetspace.entity;

import com.venus.meetspace.common.type.ActivityStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status;
    @Column(nullable = false, name = "min_participants")
    private int minParticipants;
    @Column(nullable = false, name = "max_participants")
    private int maxParticipants;

    private String image;
    private double latitude;
    private double longitude;
    @Column(name = "location", columnDefinition = "POINT")
    private Point location;

    // 自动设定location
    @PrePersist
    @PreUpdate
    public void updateLocation() {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
        this.location = factory.createPoint(new Coordinate(this.longitude, this.latitude));
    }
}
