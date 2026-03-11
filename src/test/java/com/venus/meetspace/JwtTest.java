package com.venus.meetspace;

import com.venus.meetspace.DTO.UserDTO;
import com.venus.meetspace.entity.User;
import com.venus.meetspace.util.JWTUtil;


public class JwtTest {
    public static void main(String[] args) {
        JWTUtil jwtUtil = new JWTUtil();
        UserDTO userDTO = new UserDTO();

        userDTO.setUsername("Void");
        userDTO.setRole("admin");

        System.out.println(userDTO);
        String token = jwtUtil.generateUserToken(userDTO);
        System.out.println(token);
        User user = jwtUtil.getUserFromToken(token);
        System.out.println(user);
    }
}
