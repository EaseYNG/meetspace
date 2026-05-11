package com.venus.meetspace.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.venus.meetspace.model.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

    List<Activity> findByConditions(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("longitude") Double longitude,
            @Param("latitude") Double latitude,
            @Param("radius") Double radius,
            @Param("minParticipants") Integer minParticipants,
            @Param("maxParticipants") Integer maxParticipants
    );

    List<Activity> findAllReady();

    List<Activity> findAllByIds(@Param("ids") List<Long> ids);
}
