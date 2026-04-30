package com.minimaxi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import com.minimaxi.backend.enums.UserRole;
import com.minimaxi.backend.enums.UserStatus;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('app_user_id_seq')")
    @Column(name = "id", nullable = false)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Size(max = 120)
    @NotNull
    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Size(max = 120)
    @NotNull
    @Column(name = "email", nullable = false, length = 120)
    private String email;

    @Size(max = 30)
    @Column(name = "phone", length = 30)
    private String phone;

    @Size(max = 255)
    @Column(name = "password_hash")
    private String passwordHash;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'INVITED'")
    @Column(name = "status", nullable = false)
    private UserStatus status;


    @Column(name = "avatar", columnDefinition = "TEXT")
    private String avatar;

    @Size(max = 6)
    @Column(name = "reset_otp", length = 6)
    private String resetOtp;

    @Column(name = "reset_otp_expires_at")
    private Instant resetOtpExpiresAt;
}