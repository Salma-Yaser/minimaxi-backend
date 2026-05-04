package com.minimaxi.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanySettingsResponse {
    private Long id;
    private String name;
    private String logo;
    private String timezone;
    private String language;

    @JsonProperty("service_type")
    private String serviceType;

    private String industry;

    @JsonProperty("setup_completed")
    private Boolean setupCompleted;
}