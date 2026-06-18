// =====================================================================
// CreateAssetTypeRequest.java
// =====================================================================
package com.minimaxi.backend.dto.request;

public class CreateAssetTypeRequest {
    private String name;
    private String description;
    private String industry;
    private Long organizationId;
    private Boolean active;
    private Integer maintenanceInterval;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Integer getMaintenanceInterval() { return maintenanceInterval; }
    public void setMaintenanceInterval(Integer maintenanceInterval) { this.maintenanceInterval = maintenanceInterval; }
}