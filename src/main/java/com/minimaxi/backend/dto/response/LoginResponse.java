package com.minimaxi.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private UserResponse user;
    private String token;
}