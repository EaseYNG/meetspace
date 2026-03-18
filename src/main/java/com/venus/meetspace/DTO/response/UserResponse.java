package com.venus.meetspace.DTO.response;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserResponse {
    private long id;
    private String nickname;
    private String username;
    private String password;

    // 登陆后属性(profile)
    private int age;
    private String gender;
    private String email;
    private String firstname;
    private String lastname;
}
