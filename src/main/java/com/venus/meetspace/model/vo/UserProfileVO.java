package com.venus.meetspace.model.vo;

import lombok.Data;

@Data
public class UserProfileVO {
    private Long id;
    private String nickname;
    private String username;
    private Integer age;
    private String gender;
    private String email;
    private String firstname;
    private String lastname;
}
