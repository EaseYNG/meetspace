package com.venus.meetspace.model.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Registration command")
public class RegisterCmd {

    @NotBlank(message = "Nickname must not be empty")
    @Size(min = 2, max = 50, message = "Nickname length must be 2-50 characters")
    @Schema(description = "Display nickname", example = "Void")
    private String nickname;

    @NotBlank(message = "Username must not be empty")
    @Size(min = 3, max = 32, message = "Username length must be 3-32 characters")
    @Schema(description = "Unique username", example = "voidyang")
    private String username;

    @NotBlank(message = "Password must not be empty")
    @Size(min = 6, max = 128, message = "Password length must be 6-128 characters")
    @Schema(description = "Password", example = "password123")
    private String password;
}
