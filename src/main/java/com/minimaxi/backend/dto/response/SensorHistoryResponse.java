package com.minimaxi.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SensorHistoryResponse {
    private String timestamp;
    private Double temperature;
    private Double vibration;
    private Double pressure;
}