package com.venus.meetspace.service;

import com.venus.meetspace.model.dto.LoginCmd;
import com.venus.meetspace.model.dto.RegisterCmd;
import com.venus.meetspace.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    CustomUserDetails login(LoginCmd cmd, HttpServletRequest request, HttpServletResponse response);

    void register(RegisterCmd cmd);

    void logout(HttpServletRequest request, HttpServletResponse response);
}
