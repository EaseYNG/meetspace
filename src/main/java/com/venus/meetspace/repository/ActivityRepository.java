package com.venus.meetspace.repository;

import com.venus.meetspace.entity.Activity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Optional<Activity> findById(long id);

    @Transactional
    void deleteById(long id);

    // 根据id列表返回活动列表
    @Query(value = "SELECT * FROM activity WHERE id IN (:activity_ids) ", nativeQuery = true)
    List<Activity> findAllByIds(@Param("activity_ids") List<Long> activityIds);

    // 获取表中所有READY状态的活动列表
    @Query(value = "SELECT * FROM activity WHERE status = 0", nativeQuery = true)
    List<Activity> findAllReady();

    // 根据筛选条件查询活动列表
    @Query(value = "SELECT * FROM activity e WHERE " +
            "(:radius IS NULL OR (ST_Distance_Sphere(e.location, POINT(:longitude, :latitude)) <= :radius * 1000)) " +
            "AND (:start_time IS NULL OR e.start_time >= :start_time) " +
            "AND (:end_time IS NULL OR e.end_time <= :end_time) " +
            "AND (:min IS NULL OR e.min_participants >= :min) " +
            "AND (:max IS NULL OR e.max_participants <= :max) " +
            "AND e.status == 0",
            nativeQuery = true)
    Optional<List<Activity>> findByConditions(
            @Param("start_time") LocalDateTime startTime,
            @Param("end_time") LocalDateTime endTime,
            @Param("longitude") Double longitude,
            @Param("latitude") Double latitude,
            @Param("radius") Double radius,
            @Param("min") Integer min,
            @Param("max") Integer max
    );

}
