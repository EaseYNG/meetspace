package com.venus.meetspace.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.venus.meetspace.model.entity.ActivityParticipant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityParticipantMapper extends BaseMapper<ActivityParticipant> {

    List<ActivityParticipant> findByParticipantId(@Param("participantId") Long participantId);

    int deleteByIds(@Param("activityId") Long activityId, @Param("participantId") Long participantId);
}
