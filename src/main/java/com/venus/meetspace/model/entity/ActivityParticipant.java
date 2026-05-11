package com.venus.meetspace.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.venus.meetspace.common.enums.ParticipantRole;
import lombok.Data;

@Data
@TableName("activity_participant")
public class ActivityParticipant {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("activity_id")
    private Long activityId;

    @TableField("participant_id")
    private Long participantId;

    private ParticipantRole role;
}
