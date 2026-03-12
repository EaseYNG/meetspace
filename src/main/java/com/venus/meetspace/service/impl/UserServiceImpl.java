package com.venus.meetspace.service.impl;

import com.venus.meetspace.DTO.RegisterRequest;
import com.venus.meetspace.DTO.Result;
import com.venus.meetspace.DTO.UserDTO;
import com.venus.meetspace.VO.UserVO;
import com.venus.meetspace.entity.User;
import com.venus.meetspace.exception.BusinessException;
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


    // UserDTO, UserVO, User 间转换方法
    public User toUser(UserDTO userDTO) {
        User temp = new User();
        temp.setUsername(userDTO.getUsername());
        return temp;
    }
    public UserDTO toDTO(User user) {
        UserDTO temp = new UserDTO();
        temp.setUsername(user.getUsername());
        temp.setId(user.getId());
        return temp;
    }
    public UserVO toVO(User user) {
        UserVO temp = new UserVO();
        temp.setUsername(user.getUsername());
        temp.setNickname(user.getNickname());
        return temp;
    }
}
