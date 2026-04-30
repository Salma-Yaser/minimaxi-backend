package com.minimaxi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "sensor_type")
public class SensorType {
    @Id
    @ColumnDefault("nextval('sensor_type_id_seq')")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 30)
    @NotNull
    @Column(name = "unit", nullable = false, length = 30)
    private String unit;

    @Column(name = "default_warning_threshold", precision = 10, scale = 2)
    private BigDecimal defaultWarningThreshold;

    @Column(name = "default_critical_threshold", precision = 10, scale = 2)
    private BigDecimal defaultCriticalThreshold;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}