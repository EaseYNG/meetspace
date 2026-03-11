package com.venus.meetspace.service;

import com.venus.meetspace.DTO.RegisterRequest;
import com.venus.meetspace.DTO.Result;
import com.venus.meetspace.DTO.UserDTO;
import com.venus.meetspace.entity.User;

import java.util.List;

public interface UserService {
    User findById(long id);
    User findByUsername(String username);
    Result<User> register(RegisterRequest rq);
    Result<Void> update(UserDTO userDTO);
    Result<Void> delete(UserDTO userDTO);
    List<User> getAllUsers();
}
