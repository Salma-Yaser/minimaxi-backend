package com.minimaxi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "ai_model_info")
public class AiModelInfo {
    @Id
    @ColumnDefault("nextval('ai_model_info_id_seq')")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Size(max = 120)
    @NotNull
    @Column(name = "model_name", nullable = false, length = 120)
    private String modelName;

    @Size(max = 50)
    @NotNull
    @Column(name = "version", nullable = false, length = 50)
    private String version;

    @Column(name = "features_used_json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> featuresUsedJson;

    @Column(name = "last_training_date")
    private LocalDate lastTrainingDate;

    @Size(max = 255)
    @Column(name = "notes")
    private String notes;

}