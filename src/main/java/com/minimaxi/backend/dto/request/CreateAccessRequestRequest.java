package com.minimaxi.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccessRequestRequest {

    @JsonProperty("company_name")
    private String companyName;

    private String industry;

    @JsonProperty("contact_person_name")
    private String contactPersonName;

    private String email;
    private String phone;

    @JsonProperty("requested_service")
    private String requestedService;
}