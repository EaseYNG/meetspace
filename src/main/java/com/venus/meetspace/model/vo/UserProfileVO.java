package com.venus.meetspace.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User profile view object")
public class UserProfileVO {

    @Schema(description = "User ID")
    private Long id;

    @Schema(description = "Nickname")
    private String nickname;

    @Schema(description = "Username")
    private String username;

    @Schema(description = "Age")
    private Integer age;

    @Schema(description = "Gender")
    private String gender;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "First name")
    private String firstname;

    @Schema(description = "Last name")
    private String lastname;
}
