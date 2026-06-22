package com.minimaxi.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MachinePredictionResponse {
    private String severity;
    private Double confidenceScore;
    private Double rulCycles;
    private Double ttfHours;
    private String explanation;
    private String problemSensor;
    private Double currentValue;
    private Double normalMin;
    private Double normalMax;
    private Double modelAccuracy;
    private Double modelF1Score;
}