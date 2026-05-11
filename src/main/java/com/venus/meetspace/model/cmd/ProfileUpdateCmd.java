package com.venus.meetspace.model.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Profile update command")
public class ProfileUpdateCmd {

    @Schema(description = "Age", example = "25")
    private Integer age;

    @Schema(description = "Gender", example = "Male")
    private String gender;

    @Schema(description = "Email", example = "void@example.com")
    private String email;

    @Schema(description = "First name", example = "Void")
    private String firstname;

    @Schema(description = "Last name", example = "Yang")
    private String lastname;
}
