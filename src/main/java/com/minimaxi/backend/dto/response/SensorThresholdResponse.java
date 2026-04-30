package com.minimaxi.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SensorThresholdResponse {
    private Long id;

    @JsonProperty("asset_type_id")
    private Long assetTypeId;

    @JsonProperty("asset_type_name")
    private String assetTypeName;

    @JsonProperty("sensor_type_id")
    private Long sensorTypeId;

    @JsonProperty("name")
    private String sensorTypeName;

    @JsonProperty("unit")
    private String sensorUnit;

    @JsonProperty("warningThreshold")
    private double warningValue;

    @JsonProperty("criticalThreshold")
    private double criticalValue;

    @JsonProperty("updated_at")
    private String updatedAt;

    public SensorThresholdResponse(Long id, Long assetTypeId, String assetTypeName,
                                   Long sensorTypeId, String sensorTypeName, String sensorUnit,
                                   double warningValue, double criticalValue, String updatedAt) {
        this.id = id;
        this.assetTypeId = assetTypeId;
        this.assetTypeName = assetTypeName;
        this.sensorTypeId = sensorTypeId;
        this.sensorTypeName = sensorTypeName;
        this.sensorUnit = sensorUnit;
        this.warningValue = warningValue;
        this.criticalValue = criticalValue;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public Long getAssetTypeId() { return assetTypeId; }
    public String getAssetTypeName() { return assetTypeName; }
    public Long getSensorTypeId() { return sensorTypeId; }
    public String getSensorTypeName() { return sensorTypeName; }
    public String getSensorUnit() { return sensorUnit; }
    public double getWarningValue() { return warningValue; }
    public double getCriticalValue() { return criticalValue; }
    public String getUpdatedAt() { return updatedAt; }
}