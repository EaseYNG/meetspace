package com.venus.meetspace.service.impl;

import com.venus.meetspace.dto.request.AuthRequest;
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
        userRepository.findByUsername(authDTO.getUsername())
                .orElseThrow(() -> new BusinessException(404, "用户未找到！"));
        User temp = userRepository.findByUsername(authDTO.getUsername()).get();
        if(!pe.matches(authDTO.getPassword(), temp.getPassword()))
            throw new BusinessException(401, "密码错误");

        return temp;
    }
}
