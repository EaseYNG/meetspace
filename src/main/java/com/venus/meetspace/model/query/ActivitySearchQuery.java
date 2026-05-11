package com.venus.meetspace.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Activity search query")
public class ActivitySearchQuery {

    @Schema(description = "Earliest start time filter")
    private LocalDateTime startTime;

    @Schema(description = "Latest end time filter")
    private LocalDateTime endTime;

    @Schema(description = "Longitude for geo-search", example = "116.4074")
    private Double longitude;

    @Schema(description = "Latitude for geo-search", example = "39.9042")
    private Double latitude;

    @Schema(description = "Search radius (km)", example = "10.0")
    private Double radiusKm;

    @Schema(description = "Minimum participants filter")
    private Integer minParticipants;

    @Schema(description = "Maximum participants filter")
    private Integer maxParticipants;
}
