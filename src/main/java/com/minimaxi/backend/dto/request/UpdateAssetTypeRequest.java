// =====================================================================
// UpdateAssetTypeRequest.java
// =====================================================================
package com.minimaxi.backend.dto.request;

public class UpdateAssetTypeRequest {
    private String name;
    private String description;
    private String industry;
    private Boolean active;
    private Integer maintenanceInterval;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Integer getMaintenanceInterval() { return maintenanceInterval; }
    public void setMaintenanceInterval(Integer maintenanceInterval) { this.maintenanceInterval = maintenanceInterval; }
}