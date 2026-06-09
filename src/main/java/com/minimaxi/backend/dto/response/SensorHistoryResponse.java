package com.minimaxi.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class SensorHistoryResponse {
    private String timestamp;
    private Map<String, Double> sensorValues;

    @JsonAnyGetter
    public Map<String, Double> getSensorValues() {
        return sensorValues;
    }
}