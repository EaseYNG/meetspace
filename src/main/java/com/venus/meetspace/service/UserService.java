package com.venus.meetspace.service;

import com.venus.meetspace.entity.User;

public interface UserService {
    User findById(long id);
    User findByUsername(String username);
    void save(User user);

}
