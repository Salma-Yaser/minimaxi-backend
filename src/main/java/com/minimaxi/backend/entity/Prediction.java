package com.minimaxi.backend.entity;

import com.minimaxi.backend.enums.IssueType;
import com.minimaxi.backend.enums.PredictionSeverity;
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
@Table(name = "prediction")
public class Prediction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prediction_seq")
    @SequenceGenerator(name = "prediction_seq", sequenceName = "prediction_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    @Column(name = "confidence_score", precision = 5, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "current_value", precision = 10, scale = 4)
    private BigDecimal currentValue;

    @Column(name = "normal_min", precision = 10, scale = 4)
    private BigDecimal normalMin;

    @Column(name = "normal_max", precision = 10, scale = 4)
    private BigDecimal normalMax;

    @Column(name = "model_accuracy", precision = 6, scale = 4)
    private BigDecimal modelAccuracy;

    @Column(name = "model_precision", precision = 6, scale = 4)
    private BigDecimal modelPrecision;

    @Column(name = "model_recall", precision = 6, scale = 4)
    private BigDecimal modelRecall;

    @Column(name = "model_f1_score", precision = 6, scale = 4)
    private BigDecimal modelF1Score;

    @Enumerated(EnumType.STRING)
    @Column(name = "suggested_issue_type")
    private IssueType suggestedIssueType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private PredictionSeverity severity;

    @Size(max = 50)
    @Column(name = "model_version", length = 50)
    private String modelVersion;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "predicted_at", nullable = false)
    private Instant predictedAt;

    @Column(name = "rul_cycles", precision = 10, scale = 2)
    private BigDecimal rulCycles;

    @Column(name = "explanation", length = Integer.MAX_VALUE)
    private String explanation;

    @Column(name = "ttf_hours", precision = 10, scale = 2)
    private BigDecimal ttfHours;
}