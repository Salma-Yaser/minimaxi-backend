package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.request.ActivateAccountRequest;
import com.minimaxi.backend.dto.request.LoginRequest;
import com.minimaxi.backend.dto.response.ActivateAccountResponse;
import com.minimaxi.backend.dto.response.LoginResponse;

import java.util.Map;

public interface AuthService {
    ActivateAccountResponse activateAccount(ActivateAccountRequest request);
    LoginResponse login(LoginRequest request);
    Map<String, Object> forgotPassword(String email);
    Map<String, Object> resetPassword(String email, String otp, String newPassword);
}