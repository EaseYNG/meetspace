package com.venus.meetspace.model.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ProfileUpdateCmd {

    private Integer age;
    private String gender;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String firstname;
    private String lastname;
}
