package com.minimaxi.backend.entity;

import com.minimaxi.backend.enums.MachineCriticality;
import com.minimaxi.backend.enums.MachineStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "machine")
public class Machine {
    @Id
    @ColumnDefault("nextval('machine_id_seq')")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "asset_id", nullable = false, unique = true, length = 50)
    private String assetId;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_type_id")
    private AssetType assetType;

    @Size(max = 120)
    @NotNull
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Size(max = 100)
    @Column(name = "machine_type", length = 100)
    private String machineType;

    @Size(max = 100)
    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @Size(max = 150)
    @Column(name = "location", length = 150)
    private String location;

    @Column(name = "installation_date")
    private LocalDate installationDate;
    @Column(name = "operating_cycles", precision = 10, scale = 2)
    private BigDecimal operatingCycles;

    @Column(name = "operating_hours", precision = 10, scale = 2)
    private BigDecimal operatingHours;
    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


    // Default to MEDIUM criticality and HEALTHY status for new machines
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'MEDIUM'")
    @Column(name = "criticality", nullable = false)
    private MachineCriticality criticality;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'HEALTHY'")
    @Column(name = "status", nullable = false)
    private MachineStatus status;
}