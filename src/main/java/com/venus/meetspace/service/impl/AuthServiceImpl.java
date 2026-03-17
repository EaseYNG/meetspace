package com.venus.meetspace.service.impl;

import com.venus.meetspace.DTO.request.AuthRequest;
import com.venus.meetspace.entity.User;
import com.venus.meetspace.exception.BusinessException;
import com.venus.meetspace.repository.UserRepository;
import com.venus.meetspace.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder pe;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder pe) {
        this.userRepository = userRepository;
        this.pe = pe;
    }

    @Override
    public User login(AuthRequest authDTO) {
        User temp = userRepository.findByUsername(authDTO.getUsername());
        if(!pe.matches(authDTO.getPassword(), temp.getPassword()))
            throw new BusinessException(412, "密码错误");

        return temp;
    }
}
