package com.venus.meetspace.model.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Login command")
public class LoginCmd {

    @NotBlank(message = "Username must not be empty")
    @Schema(description = "Username", example = "voidyang")
    private String username;

    @NotBlank(message = "Password must not be empty")
    @Schema(description = "Password", example = "password123")
    private String password;
}
