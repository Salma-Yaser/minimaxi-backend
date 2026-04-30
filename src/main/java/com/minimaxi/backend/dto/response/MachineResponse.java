package com.minimaxi.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class MachineResponse {
    private Long id;

    @JsonProperty("asset_id")
    private String assetId;

    private String name;
    private String type;
    private String location;

    @JsonProperty("serial_number")
    private String serialNumber;

    @JsonProperty("manufacturer")
    private String manufacturer;

    @JsonProperty("model")
    private String model;

    @JsonProperty("installation_date")
    private String installationDate;

    private String criticality;
    private String status;

    @JsonProperty("last_maintenance")
    private String lastMaintenance;

    private Map<String, Double> sensors;

    private MachinePredictionResponse prediction;
}