// =====================================================================
// CreateSensorThresholdRequest.java
// =====================================================================
package com.minimaxi.backend.dto.request;

public class CreateSensorThresholdRequest {
    private Long assetTypeId;
    private Long sensorTypeId;
    private Long organizationId;
    private Long updatedByUserId;
    private double warningValue;
    private double criticalValue;

    public Long getAssetTypeId() { return assetTypeId; }
    public void setAssetTypeId(Long assetTypeId) { this.assetTypeId = assetTypeId; }
    public Long getSensorTypeId() { return sensorTypeId; }
    public void setSensorTypeId(Long sensorTypeId) { this.sensorTypeId = sensorTypeId; }
    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
    public Long getUpdatedByUserId() { return updatedByUserId; }
    public void setUpdatedByUserId(Long updatedByUserId) { this.updatedByUserId = updatedByUserId; }
    public double getWarningValue() { return warningValue; }
    public void setWarningValue(double warningValue) { this.warningValue = warningValue; }
    public double getCriticalValue() { return criticalValue; }
    public void setCriticalValue(double criticalValue) { this.criticalValue = criticalValue; }
}