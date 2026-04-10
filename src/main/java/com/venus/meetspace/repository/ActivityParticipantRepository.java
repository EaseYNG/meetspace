package com.venus.meetspace.repository;

import com.venus.meetspace.entity.ActivityParticipant;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityParticipantRepository extends JpaRepository<ActivityParticipant, Long> {
    Optional<ActivityParticipant> findByActivityIdAndParticipantId(
            Long activityId, Long participantId);

    // 按状态顺序排序返回List
    @Query("SELECT ap FROM ActivityParticipant ap " +
            "JOIN Activity a ON ap.activityId = a.id " +
            "WHERE ap.participantId = :participantId " +
            "ORDER BY " +
            "CASE a.status " +
            "   WHEN 'READY' THEN 1 " +
            "   WHEN 'CLOSED' THEN 2 " +
            "   WHEN 'OVER' THEN 3 " +
            "   ELSE 4 END, " +
            "a.startTime DESC")
    Optional<List<ActivityParticipant>> findByParticipantId(@Param("participantId") Long participantId);

    @Transactional
    void deleteById(Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM ActivityParticipant ap WHERE ap.activityId = :activityId AND ap.participantId = :participantId")
    void deleteByIds(@Param("activityId") Long activityId, @Param("participantId") Long ParticipantId);

}
