package com.venus.meetspace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "activity_participant")
public class ActivityParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "activity_id")
    private long activityId;
    @Column(name = "participant_id")
    private Long participantId;
}
