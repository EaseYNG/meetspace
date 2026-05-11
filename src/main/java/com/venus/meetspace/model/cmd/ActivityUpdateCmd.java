package com.venus.meetspace.model.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Activity update command (all fields optional)")
public class ActivityUpdateCmd {

    @Schema(description = "Activity title")
    private String title;

    @Schema(description = "Activity start time")
    private LocalDateTime startTime;

    @Schema(description = "Activity end time")
    private LocalDateTime endTime;

    @Schema(description = "Signup deadline")
    private LocalDateTime signupDeadline;

    @Schema(description = "Activity address")
    private String address;

    @Schema(description = "Cover image URL")
    private String image;

    @Schema(description = "Activity description")
    private String description;
}
