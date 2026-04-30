package com.minimaxi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import com.minimaxi.backend.enums.IssueSource;
import com.minimaxi.backend.enums.IssueStatus;
import com.minimaxi.backend.enums.PredictionSeverity;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "issue")
public class Issue {
    @Id
    @ColumnDefault("nextval('issue_id_seq')")
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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private AppUser createdByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prediction_id")
    private Prediction prediction;

    @Size(max = 200)
    @NotNull
    @Column(name = "summary", nullable = false, length = 200)
    private String summary;

    @Column(name = "details", length = Integer.MAX_VALUE)
    private String details;
    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private IssueSource source;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private PredictionSeverity severity;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'OPEN'")
    @Column(name = "status", nullable = false)
    private IssueStatus status;
}