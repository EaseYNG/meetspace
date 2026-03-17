package com.venus.meetspace.service;

import com.venus.meetspace.DTO.request.AuthRequest;
import com.venus.meetspace.entity.User;


public interface AuthService {
    User login(AuthRequest authDTO);
}
