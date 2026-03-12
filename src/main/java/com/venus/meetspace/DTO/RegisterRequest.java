package com.venus.meetspace.DTO;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nickname;
    private String username;
    private String password;
}
