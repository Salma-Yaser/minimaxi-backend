package com.minimaxi.backend.controller;

import com.minimaxi.backend.dto.request.ActivateAccountRequest;
import com.minimaxi.backend.dto.request.CreateAccessRequestRequest;
import com.minimaxi.backend.dto.request.LoginRequest;
import com.minimaxi.backend.dto.response.AccessRequestResponse;
import com.minimaxi.backend.dto.response.ActivateAccountResponse;
import com.minimaxi.backend.dto.response.LoginResponse;
import com.minimaxi.backend.service.AuthService;
import com.minimaxi.backend.service.OrganizationRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;
    private final OrganizationRequestService organizationRequestService;

    public AuthController(AuthService authService,
                          OrganizationRequestService organizationRequestService) {
        this.authService = authService;
        this.organizationRequestService = organizationRequestService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/activate")
    public ActivateAccountResponse activateAccount(@RequestBody ActivateAccountRequest request) {
        return authService.activateAccount(request);
    }

    @PostMapping("/request-access")
    public AccessRequestResponse requestAccess(@RequestBody CreateAccessRequestRequest request) {
        return organizationRequestService.createAccessRequest(request);
    }

    @PostMapping("/logout")
    public Map<String, Object> logout() {
        return Map.of("success", true);
    }

    // ✅ Forgot Password
    @PostMapping("/forgot-password")
    public Map<String, Object> forgotPassword(@RequestBody Map<String, String> body) {
        return authService.forgotPassword(body.get("email"));
    }

    // ✅ Reset Password
    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> body) {
        return authService.resetPassword(
                body.get("email"),
                body.get("otp"),
                body.get("newPassword")
        );
    }
}