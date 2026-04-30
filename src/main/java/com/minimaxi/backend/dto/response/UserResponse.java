package com.minimaxi.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    // password is missing here
    private String role;

    private String avatar;

    @JsonProperty("first_login")
    private Boolean firstLogin;

    @JsonProperty("company_id")
    private Long companyId;

    @JsonProperty("created_at")
    private String createdAt;

    //this field not in mock data
    private String status;
}