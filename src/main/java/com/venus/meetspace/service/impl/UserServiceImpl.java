package com.venus.meetspace.service.impl;

import com.venus.meetspace.dto.request.RegisterRequest;
import com.venus.meetspace.dto.Profile;
import com.venus.meetspace.entity.User;
import com.venus.meetspace.exception.BusinessException;
import com.venus.meetspace.repository.UserRepository;
import com.venus.meetspace.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder pe;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder pe) {
        this.userRepository = userRepository;
        this.pe = pe;
    }

    @Override
    public void register(RegisterRequest rq) {
        // 用户名已存在则抛出异常
        this.userRepository.findByUsername(rq.getUsername())
                .ifPresent(u -> { throw new BusinessException(409, "用户名已被注册！"); });
        // 用户名不存在，创建新用户
        User user = new User();
        user.setNickname(rq.getNickname());
        user.setUsername(rq.getUsername());
        user.setPassword(pe.encode(rq.getPassword()));
        userRepository.save(user);
        log.info("注册成功! " + user.getId());
    }
    @Override
    public void setProfile(Profile temp, long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用户未找到！"));
        user.setAge(temp.getAge());
        user.setGender(temp.getGender());
        user.setEmail(temp.getEmail());
        user.setFirstname(temp.getFirstname());
        user.setLastname(temp.getLastname());

        userRepository.save(user);
        log.info("设置个人资料: user_id: " + user.getId());
    }

    @Override
    public Profile getProfile(long id) {
        Profile profile = new Profile();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用户未找到！"));

        profile.setAge(user.getAge());
        profile.setGender(user.getGender());
        profile.setEmail(user.getEmail());
        profile.setFirstname(user.getFirstname());
        profile.setLastname(user.getLastname());
        log.info("获取个人资料: user_id: " + user.getId());
        return profile;
    }
}
