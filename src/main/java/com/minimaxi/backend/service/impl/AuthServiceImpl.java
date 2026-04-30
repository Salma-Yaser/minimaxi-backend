package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.config.JwtUtil;
import com.minimaxi.backend.dto.request.ActivateAccountRequest;
import com.minimaxi.backend.dto.request.LoginRequest;
import com.minimaxi.backend.dto.response.ActivateAccountResponse;
import com.minimaxi.backend.dto.response.LoginResponse;
import com.minimaxi.backend.dto.response.UserResponse;
import com.minimaxi.backend.entity.AppUser;
import com.minimaxi.backend.entity.Organization;
import com.minimaxi.backend.entity.OrganizationRequest;
import com.minimaxi.backend.enums.OrganizationStatus;
import com.minimaxi.backend.enums.RequestStatus;
import com.minimaxi.backend.enums.UserRole;
import com.minimaxi.backend.enums.UserStatus;
import com.minimaxi.backend.repository.AppUserRepository;
import com.minimaxi.backend.repository.OrganizationRepository;
import com.minimaxi.backend.repository.OrganizationRequestRepository;
import com.minimaxi.backend.service.AuthService;
import com.minimaxi.backend.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final OrganizationRequestRepository organizationRequestRepository;
    private final OrganizationRepository organizationRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final EmailService emailService;

    public AuthServiceImpl(
            OrganizationRequestRepository organizationRequestRepository,
            OrganizationRepository organizationRepository,
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            EmailService emailService
    ) {
        this.organizationRequestRepository = organizationRequestRepository;
        this.organizationRepository = organizationRepository;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @Override
    public ActivateAccountResponse activateAccount(ActivateAccountRequest request) {

        OrganizationRequest accessRequest = organizationRequestRepository
                .findById(request.getAccessRequestId())
                .orElseThrow(() -> new RuntimeException("Access request not found"));

        Organization organization = new Organization();
        organization.setCompanyName(accessRequest.getCompanyName());
        organization.setIndustry(accessRequest.getIndustry());
        organization.setContactPersonName(accessRequest.getContactPersonName());
        organization.setEmail(accessRequest.getEmail());
        organization.setPhone(accessRequest.getPhone());
        organization.setRequestedService(accessRequest.getRequestedService());
        organization.setStatus(OrganizationStatus.ACTIVE);
        organization.setCreatedAt(Instant.now());
        organization.setOnboardingCompleted(false);

        Organization savedOrganization = organizationRepository.save(organization);

        AppUser user = new AppUser();
        user.setOrganization(savedOrganization);
        user.setFullName(accessRequest.getContactPersonName());
        user.setEmail(accessRequest.getEmail());
        user.setPhone(accessRequest.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(Instant.now());
        user.setRole(UserRole.COMPANY_ADMIN);
        user.setStatus(UserStatus.ACTIVE);

        AppUser savedUser = appUserRepository.save(user);

        accessRequest.setStatus(RequestStatus.APPROVED);
        accessRequest.setReviewedAt(Instant.now());
        organizationRequestRepository.save(accessRequest);

        String token = jwtUtil.generateToken(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );

        return ActivateAccountResponse.builder()
                .success(true)
                .message("Account activated successfully")
                .user(UserResponse.builder()
                        .id(savedUser.getId())
                        .name(savedUser.getFullName())
                        .email(savedUser.getEmail())
                        .role(savedUser.getRole().name().toLowerCase())
                        .firstLogin(true)
                        .companyId(savedOrganization.getId())
                        .createdAt(savedUser.getCreatedAt().toString())
                        .status(savedUser.getStatus().name().toLowerCase())
                        .build())
                .token(token)
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        AppUser user = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (user.getPasswordHash() == null ||
                !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : "USER"
        );

        return LoginResponse.builder()
                .user(UserResponse.builder()
                        .id(user.getId())
                        .name(user.getFullName())
                        .email(user.getEmail())
                        .role(user.getRole() != null ? user.getRole().name().toLowerCase() : null)
                        .firstLogin(false)
                        .companyId(user.getOrganization() != null ? user.getOrganization().getId() : null)
                        .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                        .status(user.getStatus() != null ? user.getStatus().name().toLowerCase() : null)
                        .build())
                .token(token)
                .build();
    }
    @Override
    public Map<String, Object> forgotPassword(String email) {
        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        // جنرت OTP 6 أرقام
        String otp = String.format("%06d", (int)(Math.random() * 1000000));

        // حفظ الـ OTP في الـ DB مع expiry 15 دقيقة
        user.setResetOtp(otp);
        user.setResetOtpExpiresAt(Instant.now().plusSeconds(900));
        appUserRepository.save(user);

        // بعت الإيميل
        emailService.sendOtpEmail(email, user.getFullName(), otp);

        return Map.of(
                "success", true,
                "message", "OTP sent to " + email
        );
    }

    @Override
    public Map<String, Object> resetPassword(String email, String otp, String newPassword) {
        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        // تحقق من الـ OTP
        if (user.getResetOtp() == null || !user.getResetOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        // تحقق من الـ expiry
        if (user.getResetOtpExpiresAt() == null ||
                Instant.now().isAfter(user.getResetOtpExpiresAt())) {
            throw new RuntimeException("OTP has expired");
        }

        // حدّث الـ password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetOtp(null);
        user.setResetOtpExpiresAt(null);
        appUserRepository.save(user);

        return Map.of(
                "success", true,
                "message", "Password reset successfully"
        );
    }
}