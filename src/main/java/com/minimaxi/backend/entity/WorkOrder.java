package com.minimaxi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import com.minimaxi.backend.enums.WorkOrderPriority;
import com.minimaxi.backend.enums.WorkOrderStatus;


import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "work_order")
public class WorkOrder {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('work_order_id_seq')")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private AppUser createdByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_user_id")
    private AppUser assignedToUser;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Size(max = 150)
    @NotNull
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;
    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;



    @NotNull
    @ColumnDefault("false")
    @Column(name = "ai_suggested", nullable = false)
    private Boolean aiSuggested = false;
    @Column(name = "closed_at")
    private Instant closedAt;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'MEDIUM'")
    @Column(name = "priority", nullable = false)
    private WorkOrderPriority priority;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'OPEN'")
    @Column(name = "status", nullable = false)
    private WorkOrderStatus status;
}