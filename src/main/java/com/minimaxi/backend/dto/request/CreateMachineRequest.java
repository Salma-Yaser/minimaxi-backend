package com.minimaxi.backend.dto.request;

import java.util.List;

public class CreateMachineRequest {
    private Long organizationId;
    private String name;
    private String type;
    private String location;
    private String serialNumber;
    private String criticality;
    private String installationDate;

    // ✅ حقول جديدة
    private String gatewayUrl;
    private Integer pollingIntervalSeconds;
    private List<SensorConfig> sensors;

    public static class SensorConfig {
        private Long sensorTypeId;
        private Double warningThreshold;
        private Double criticalThreshold;

        public Long getSensorTypeId() { return sensorTypeId; }
        public void setSensorTypeId(Long sensorTypeId) { this.sensorTypeId = sensorTypeId; }
        public Double getWarningThreshold() { return warningThreshold; }
        public void setWarningThreshold(Double warningThreshold) { this.warningThreshold = warningThreshold; }
        public Double getCriticalThreshold() { return criticalThreshold; }
        public void setCriticalThreshold(Double criticalThreshold) { this.criticalThreshold = criticalThreshold; }
    }

    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public String getCriticality() { return criticality; }
    public void setCriticality(String criticality) { this.criticality = criticality; }
    public String getInstallationDate() { return installationDate; }
    public void setInstallationDate(String installationDate) { this.installationDate = installationDate; }
    public String getGatewayUrl() { return gatewayUrl; }
    public void setGatewayUrl(String gatewayUrl) { this.gatewayUrl = gatewayUrl; }
    public Integer getPollingIntervalSeconds() { return pollingIntervalSeconds; }
    public void setPollingIntervalSeconds(Integer pollingIntervalSeconds) { this.pollingIntervalSeconds = pollingIntervalSeconds; }
    public List<SensorConfig> getSensors() { return sensors; }
    public void setSensors(List<SensorConfig> sensors) { this.sensors = sensors; }
}