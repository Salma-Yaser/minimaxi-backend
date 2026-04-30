package com.minimaxi.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccessRequestResponse {
    private Long id;

    @JsonProperty("company_name")
    private String companyName;

    private String industry;

    @JsonProperty("contact_person_name")
    private String contactPersonName;

    private String email;
    private String phone;

    @JsonProperty("requested_service")
    private String requestedService;

    private String status;

    @JsonProperty("created_at")
    private String createdAt;
}