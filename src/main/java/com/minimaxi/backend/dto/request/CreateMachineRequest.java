package com.minimaxi.backend.dto.request;

public class CreateMachineRequest {
    private Long organizationId;
    private String name;
    private String type;
    private String location;
    private String serialNumber;
    private String criticality;
    private String installationDate;

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
}