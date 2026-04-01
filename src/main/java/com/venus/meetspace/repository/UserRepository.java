package com.venus.meetspace.repository;

import com.venus.meetspace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(long id);
    Optional<User> findByUsername(String username);
    void deleteById(long id);
    void deleteByUsername(String username);
}
