package com.venus.meetspace.model.cmd;

import lombok.Data;

@Data
public class ProfileUpdateCmd {

    private Integer age;
    private String gender;
    private String email;
    private String firstname;
    private String lastname;
}
