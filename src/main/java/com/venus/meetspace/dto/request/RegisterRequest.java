package com.venus.meetspace.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nickname;
    private String username;
    private String password;
}
