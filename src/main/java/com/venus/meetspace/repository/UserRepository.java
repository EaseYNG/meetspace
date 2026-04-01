package com.venus.meetspace.repository;

import com.venus.meetspace.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findById(long id);
    Optional<User> findByUsername(String username);
    void deleteById(long id);
    void deleteByUsername(String username);
}
