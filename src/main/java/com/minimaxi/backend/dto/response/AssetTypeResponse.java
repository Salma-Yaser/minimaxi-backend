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

    @JsonProperty("created_at")
    private String createdAt;

    public AssetTypeResponse(Long id, String name, String description,
                             String industry, String createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.industry = industry;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIndustry() { return industry; }
    public String getCreatedAt() { return createdAt; }
}