package com.venus.meetspace.model.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Agent recommend command")
public class AgentRecommendCmd {

    @Schema(description = "User interest tags", example = "[\"Sports\", \"Outdoor\", \"Social\"]")
    private List<String> interests;

    @Schema(description = "Number of recommendations", example = "5", defaultValue = "5")
    private Integer topK = 5;

    @Schema(description = "Latitude (location preference)", example = "39.9042")
    private Double latitude;

    @Schema(description = "Longitude (location preference)", example = "116.4074")
    private Double longitude;

    @Schema(description = "Search radius (km)", example = "10.0")
    private Double radiusKm;
}
