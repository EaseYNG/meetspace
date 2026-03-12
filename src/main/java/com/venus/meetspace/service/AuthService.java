package com.venus.meetspace.service;

import com.venus.meetspace.DTO.AuthDTO;
import com.venus.meetspace.entity.User;


public interface AuthService {
    User login(AuthDTO authDTO);
}
