package com.minimaxi.backend.entity;

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
@Table(name = "work_order_completion")
public class WorkOrderCompletion {
    @Id
    @ColumnDefault("nextval('work_order_completion_id_seq')")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "completed_by_user_id", nullable = false)
    private AppUser completedByUser;

    @NotNull
    @Column(name = "action_taken", nullable = false, length = Integer.MAX_VALUE)
    private String actionTaken;

    @Size(max = 150)
    @Column(name = "root_cause", length = 150)
    private String rootCause;

    @NotNull
    @Column(name = "time_spent_minutes", nullable = false)
    private Integer timeSpentMinutes;

    @Column(name = "additional_notes", length = Integer.MAX_VALUE)
    private String additionalNotes;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;

}