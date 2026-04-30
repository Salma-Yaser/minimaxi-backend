package com.minimaxi.backend.entity;

import com.minimaxi.backend.enums.OrganizationStatus;
import com.minimaxi.backend.enums.RequestedService;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "organization")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('organization_id_seq')")
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 150)
    @NotNull
    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    @Size(max = 100)
    @Column(name = "industry", length = 100)
    private String industry;

    @Size(max = 120)
    @Column(name = "contact_person_name", length = 120)
    private String contactPersonName;

    @Size(max = 120)
    @Column(name = "email", length = 120)
    private String email;

    @Size(max = 30)
    @Column(name = "phone", length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "requested_service")
    private RequestedService requestedService;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ACTIVE'")
    @Column(name = "status", nullable = false)
    private OrganizationStatus status;

    @Size(max = 50)
    @Column(name = "timezone", length = 50)
    private String timezone;

    @Size(max = 255)
    @Column(name = "logo_url")
    private String logoUrl;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "onboarding_completed", nullable = false)
    private Boolean onboardingCompleted = false;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;
}