package com.minimaxi.backend.entity;

import com.minimaxi.backend.enums.RequestStatus;
import com.minimaxi.backend.enums.RequestedService;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "organization_request")
public class OrganizationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @NotNull
    @Column(name = "contact_person_name", nullable = false, length = 120)
    private String contactPersonName;

    @Size(max = 120)
    @NotNull
    @Column(name = "email", nullable = false, length = 120)
    private String email;

    @Size(max = 30)
    @Column(name = "phone", length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "requested_service", nullable = false)
    private RequestedService requestedService;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;
}