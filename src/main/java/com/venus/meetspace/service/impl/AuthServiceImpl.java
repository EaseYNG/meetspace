package com.venus.meetspace.service.impl;

import com.venus.meetspace.common.enums.ResultCode;
import com.venus.meetspace.common.exception.BusinessException;
import com.venus.meetspace.model.cmd.LoginCmd;
import com.venus.meetspace.model.cmd.RegisterCmd;
import com.venus.meetspace.model.entity.User;
import com.venus.meetspace.repository.UserMapper;
import com.venus.meetspace.security.CustomUserDetails;
import com.venus.meetspace.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SecurityContextRepository securityContextRepository;

    public AuthServiceImpl(UserMapper userMapper,
                           AuthenticationManager authenticationManager,
                           PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.securityContextRepository = new HttpSessionSecurityContextRepository();
    }

    @Override
    public CustomUserDetails login(LoginCmd cmd, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(cmd.getUsername(), cmd.getPassword());

        Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, request, response);

        log.info("User logged in: {}", cmd.getUsername());
        return (CustomUserDetails) authentication.getPrincipal();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterCmd cmd) {
        if (userMapper.findByUsername(cmd.getUsername()) != null) {
            throw new BusinessException(ResultCode.CONFLICT, "Username already exists");
        }

        User user = new User();
        user.setNickname(cmd.getNickname());
        user.setUsername(cmd.getUsername());
        user.setPassword(passwordEncoder.encode(cmd.getPassword()));

        userMapper.insert(user);
        log.info("User registered: id={}, username={}", user.getId(), cmd.getUsername());
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            log.warn("No user is currently authenticated");
            throw new BusinessException(ResultCode.UNAUTHORIZED, "No user is currently authenticated");
        }
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        log.info("User logged out");
    }
}
