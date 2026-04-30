package com.minimaxi.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateAccountRequest {

    @JsonProperty("access_request_id")
    private Long accessRequestId;

    private String password;
}