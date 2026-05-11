package com.venus.meetspace.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Agent recommend view object")
public class AgentRecommendVO {

    @Schema(description = "Recommended activities")
    private List<ActivityVO> recommendations;

    @Schema(description = "Reason for each recommendation")
    private List<String> reasons;

    @Schema(description = "Model used", example = "gpt-4-turbo")
    private String modelUsed;

    @Schema(description = "Confidence score", example = "0.85")
    private Double confidence;
}
