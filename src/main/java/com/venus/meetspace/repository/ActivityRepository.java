package com.venus.meetspace.repository;

import com.venus.meetspace.entity.Activity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends CrudRepository<Activity, Long> {
    Activity findById(long id);
    void deleteById(long id);
    List<Activity> findByOwnerId(long id);
}
