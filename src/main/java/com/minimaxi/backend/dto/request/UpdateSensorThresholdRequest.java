// =====================================================================
// UpdateSensorThresholdRequest.java
// =====================================================================
package com.minimaxi.backend.dto.request;

public class UpdateSensorThresholdRequest {
    private Long updatedByUserId;
    private double warningValue;
    private double criticalValue;

    public Long getUpdatedByUserId() { return updatedByUserId; }
    public void setUpdatedByUserId(Long updatedByUserId) { this.updatedByUserId = updatedByUserId; }
    public double getWarningValue() { return warningValue; }
    public void setWarningValue(double warningValue) { this.warningValue = warningValue; }
    public double getCriticalValue() { return criticalValue; }
    public void setCriticalValue(double criticalValue) { this.criticalValue = criticalValue; }
}
