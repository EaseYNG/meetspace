package com.venus.meetspace.model.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Activity creation command")
public class ActivityCreateCmd {

    @NotBlank(message = "Activity title must not be empty")
    @Schema(description = "Activity title", example = "Weekend Basketball")
    private String title;

    @NotNull(message = "Start time must not be empty")
    @Future(message = "Start time must be in the future")
    @Schema(description = "Activity start time", example = "2026-05-15T14:00:00")
    private LocalDateTime startTime;

    @NotNull(message = "End time must not be empty")
    @Future(message = "End time must be in the future")
    @Schema(description = "Activity end time", example = "2026-05-15T16:00:00")
    private LocalDateTime endTime;

    @NotNull(message = "Signup deadline must not be empty")
    @Schema(description = "Signup deadline", example = "2026-05-14T23:59:59")
    private LocalDateTime signupDeadline;

    @NotBlank(message = "Address must not be empty")
    @Schema(description = "Activity address", example = "Beijing Sports Center")
    private String address;

    @Min(value = 0, message = "Minimum participants must not be negative")
    @Schema(description = "Minimum participants", example = "2", defaultValue = "0")
    private Integer minParticipants;

    @Min(value = 1, message = "Maximum participants must be at least 1")
    @Schema(description = "Maximum participants", example = "20", defaultValue = "10")
    private Integer maxParticipants;

    @Schema(description = "Cover image URL")
    private String image;

    @Schema(description = "Activity description")
    private String description;
}
