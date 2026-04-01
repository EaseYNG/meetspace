package com.venus.meetspace.repository;

import com.venus.meetspace.entity.ActivityParticipant;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityParticipantRepository extends CrudRepository<ActivityParticipant, Long> {
    ActivityParticipant findByActivityId(Long activityId);
    List<ActivityParticipant> findByParticipantId(Long participantId);
    void deleteById(Long id);

    @Modifying
    @Query("DELETE FROM ActivityParticipant ap WHERE ap.activityId = : activityId AND ap.participantId = : participantId")
    void deleteByIds(@Param("activityId") Long activityId, @Param("participantId") Long ParticipantId);
}
