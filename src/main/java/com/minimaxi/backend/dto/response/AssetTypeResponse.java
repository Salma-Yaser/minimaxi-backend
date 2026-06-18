// =====================================================================
// AssetTypeResponse.java
// =====================================================================
package com.minimaxi.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetTypeResponse {
    private Long id;
    private String name;
    private String description;
    private String industry;
    private Boolean active;

    @JsonProperty("maintenanceInterval")
    private Integer maintenanceInterval;

    @JsonProperty("created_at")
    private String createdAt;

    public AssetTypeResponse(Long id, String name, String description,
                             String industry, Boolean active, Integer maintenanceInterval,
                             String createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.industry = industry;
        this.active = active;
        this.maintenanceInterval = maintenanceInterval;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIndustry() { return industry; }
    public Boolean getActive() { return active; }
    public Integer getMaintenanceInterval() { return maintenanceInterval; }
    public String getCreatedAt() { return createdAt; }
}