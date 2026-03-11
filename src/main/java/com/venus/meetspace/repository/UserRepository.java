package com.venus.meetspace.repository;

import com.venus.meetspace.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findById(long id);
    User findByUsername(String username);
    void deleteById(long id);
    void deleteByUsername(String username);
}
