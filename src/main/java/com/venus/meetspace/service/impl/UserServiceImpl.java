package com.venus.meetspace.service.impl;

import com.venus.meetspace.DTO.RegisterRequest;
import com.venus.meetspace.DTO.Result;
import com.venus.meetspace.DTO.UserDTO;
import com.venus.meetspace.entity.User;
import com.venus.meetspace.repository.UserRepository;
import com.venus.meetspace.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Override
    public Result<User> register(RegisterRequest rq) {
        if(this.userRepository.findByUsername(rq.getUsername()) != null) return Result.fail("用户已存在");

        User user = new User();
        user.setUsername(rq.getUsername());
        user.setPassword(pe.encode(rq.getPassword()));
        userRepository.save(user);
        return Result.success(user, "用户创建成功！");
    }

    @Override
    public Result<Void> update(UserDTO userDTO) {
        return null;
    }

    @Override
    public Result<Void> delete(UserDTO userDTO) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }


    // UserDTO to User
    private User toUser(UserDTO userDTO) {
        return null;
    }
}
