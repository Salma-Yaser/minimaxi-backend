package com.minimaxi.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MachinePredictionResponse {
    private Double failure_probability;
    private Double rul;
    private String ttf;
    private String status;
    private String recommendation;
}