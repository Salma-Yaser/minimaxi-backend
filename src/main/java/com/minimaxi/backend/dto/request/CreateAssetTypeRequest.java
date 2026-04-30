// =====================================================================
// CreateAssetTypeRequest.java
// =====================================================================
package com.minimaxi.backend.dto.request;

public class CreateAssetTypeRequest {
    private String name;
    private String description;
    private String industry;
    private Long organizationId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
}