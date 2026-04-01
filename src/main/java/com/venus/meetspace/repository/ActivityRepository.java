package com.venus.meetspace.repository;

import com.venus.meetspace.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Optional<Activity> findById(long id);
    void deleteById(long id);
    Optional<List<Activity>> findByOwnerId(long id);
}
