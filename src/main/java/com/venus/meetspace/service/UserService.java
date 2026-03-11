package com.venus.meetspace.service;

import com.venus.meetspace.DTO.Result;
import com.venus.meetspace.DTO.UserDTO;
import com.venus.meetspace.entity.User;

public interface UserService {
    User findById(long id);
    User findByUsername(String username);
    Result<Void> create(UserDTO UserDTO) throws Exception;
    Result<Void> update(UserDTO UserDTO);
    Result<Void> deleteById(long id);

}
