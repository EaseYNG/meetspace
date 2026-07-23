package com.venus.meetspace.model.cmd;


import lombok.Data;

@Data
public class RegisterCmd {

    private String nickname;
    private String username;
    private String password;
}
