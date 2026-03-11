package com.venus.meetspace.service.impl;

import com.venus.meetspace.DTO.Result;
import com.venus.meetspace.DTO.UserDTO;
import com.venus.meetspace.entity.User;
import com.venus.meetspace.repository.UserRepository;
import com.venus.meetspace.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public Result<Void> create(UserDTO userDTO) throws Exception {
        try {
            this.userRepository.save(toUser(userDTO));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Result.fail(e.getMessage());
        }
        return Result.success(null);
    }

    @Override
    public Result<Void> update(UserDTO userDTO) {
        return null;
    }

    @Override
    public Result<Void> deleteById(long id) {
        return null;
    }


    // UserDTO to User
    private User toUser(UserDTO userDTO) {
        User user = new User();


        return user;
    }
}
