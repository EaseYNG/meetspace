package com.venus.meetspace.service.impl;

import com.venus.meetspace.DTO.request.RegisterRequest;
import com.venus.meetspace.DTO.Profile;
import com.venus.meetspace.entity.User;
import com.venus.meetspace.exception.BusinessException;
import com.venus.meetspace.repository.UserRepository;
import com.venus.meetspace.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder pe;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder pe) {
        this.userRepository = userRepository;
        this.pe = pe;
    }

    @Override
    public User findById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        users.addAll((Collection<? extends User>) userRepository.findAll());
        return users;
    }

    @Override
    public User register(RegisterRequest rq) {
        if(this.userRepository.findByUsername(rq.getUsername()) != null) {
            throw new BusinessException(411, "用户已存在"); // 用户已存在
        }

        User user = new User();
        user.setNickname(rq.getNickname());
        user.setUsername(rq.getUsername());
        user.setPassword(pe.encode(rq.getPassword()));
        userRepository.save(user);
        return user;
    }
    @Override
    public void setProfile(Profile temp, long id) {
        User user = this.findById(id);

        user.setAge(temp.getAge());
        user.setGender(temp.getGender());
        user.setEmail(temp.getEmail());
        user.setFirstname(temp.getFirstname());
        user.setLastname(temp.getLastname());
        
        userRepository.save(user);
    }

    @Override
    public Profile getProfile(long id) {
        Profile profile = new Profile();
        User user = this.findById(id);

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
