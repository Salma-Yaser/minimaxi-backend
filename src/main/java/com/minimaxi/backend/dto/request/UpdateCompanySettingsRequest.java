package com.minimaxi.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCompanySettingsRequest {
    private String name;
    private String logo;
    private String timezone;
    private String language;
    private String service_type;
    private String industry;
}