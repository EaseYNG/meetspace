package com.venus.meetspace.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private long id;
    private String nickname;
    private String username;

    // 登陆后属性(profile)
    private int age;
    private String gender;
    private String email;
    private String firstname;
    private String lastname;
}
