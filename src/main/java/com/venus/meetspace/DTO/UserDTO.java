package com.venus.meetspace.DTO;

import lombok.Data;

@Data
public class UserDTO {
    private long id;
    private String token;
    private String username;
    private String password;
    private String nickname;

    private int age;
    private String gender;
    private String email;
    private String firstname;
    private String lastname;
}
