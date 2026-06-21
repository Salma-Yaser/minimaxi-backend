package com.minimaxi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import com.minimaxi.backend.enums.DevicePlatform;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_device_token")
public class UserDeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "device_platform", nullable = false)
    private DevicePlatform devicePlatform;

    @NotNull
    @Size(max = 255)
    @Column(name = "device_token", nullable = false, unique = true, length = 255)
    private String deviceToken;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;
}
