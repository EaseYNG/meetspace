package com.venus.meetspace.service.impl;

import com.venus.meetspace.dto.request.RegisterRequest;
import com.venus.meetspace.dto.Profile;
import com.venus.meetspace.entity.User;
import com.venus.meetspace.exception.BusinessException;
import com.venus.meetspace.repository.UserRepository;
import com.venus.meetspace.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder pe;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder pe) {
        this.userRepository = userRepository;
        this.pe = pe;
    }

    @Override
    public void register(RegisterRequest rq) {
        User user = this.userRepository.findByUsername(rq.getUsername())
                .orElseThrow(() -> new BusinessException(405, "用户不存在！"));
        user.setNickname(rq.getNickname());
        user.setUsername(rq.getUsername());
        user.setPassword(pe.encode(rq.getPassword()));
        userRepository.save(user);
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

        userRepository.save(user); // 保存至db
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

        return profile;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }
}
