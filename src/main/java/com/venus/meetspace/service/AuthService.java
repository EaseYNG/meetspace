package com.venus.meetspace.service;

import com.venus.meetspace.DTO.Result;
import com.venus.meetspace.entity.User;

public interface AuthService {
    Result<User> loginSuccess();
    Result<Void> loginFail();
}
