package com.venus.meetspace.service.impl;

import com.venus.meetspace.dto.request.AuthRequest;
import com.venus.meetspace.entity.User;
import com.venus.meetspace.exception.BusinessException;
import com.venus.meetspace.repository.UserRepository;
import com.venus.meetspace.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder pe;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder pe) {
        this.userRepository = userRepository;
        this.pe = pe;
    }

    @Override
    public User login(AuthRequest authDTO) {
        User temp = userRepository.findByUsername(authDTO.getUsername())
                .orElseThrow(() -> new BusinessException(404, "用户未找到！"));
        if(!pe.matches(authDTO.getPassword(), temp.getPassword()))
            throw new BusinessException(401, "密码错误");
        log.info("登录成功! " + temp.getId());
        return temp;
    }
}
