package com.minimaxi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "sensor_reading")
public class SensorReading {
    @Id
    @ColumnDefault("nextval('sensor_reading_id_seq')")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @NotNull
    @Column(name = "value", nullable = false)
    private Double value;

    @NotNull
    @Column(name = "reading_time", nullable = false)
    private Instant readingTime;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "ingested_at", nullable = false)
    private Instant ingestedAt;

/*
 TODO [Reverse Engineering] create field to map the 'quality' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @ColumnDefault("'VALID'")
    @Column(name = "quality", columnDefinition = "reading_quality_enum not null")
    private Object quality;
*/
}