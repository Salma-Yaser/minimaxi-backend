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
@Table(name = "work_order_rating")
public class WorkOrderRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // WorkOrder واحدة ميتقيمش غير مرة واحدة
    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false, unique = true)
    private WorkOrder workOrder;

    // مين قيّم (المفروض الـ engineer/creator اللي استلم notification الإكمال)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rated_by_user_id", nullable = false)
    private AppUser ratedByUser;

    // مين اتقيّم (الـ technician اللي عمل الـ work order)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "technician_user_id", nullable = false)
    private AppUser technicianUser;

    // 1-5 نجوم
    @NotNull
    @Column(name = "stars", nullable = false)
    private Integer stars;

    @Column(name = "feedback", length = Integer.MAX_VALUE)
    private String feedback;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}