package com.minimaxi.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActivateAccountResponse {
    private Boolean success;
    private String message;
    private UserResponse user;
    private String token;
}