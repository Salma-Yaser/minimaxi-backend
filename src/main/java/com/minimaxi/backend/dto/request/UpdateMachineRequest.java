package com.minimaxi.backend.dto.request;

public class UpdateMachineRequest {
    private String name;
    private String type;
    private String location;
    private String serialNumber;
    private String criticality;
    private String status;

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}