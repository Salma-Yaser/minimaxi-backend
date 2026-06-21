package com.minimaxi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import com.minimaxi.backend.enums.ChatStatus;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "chat_conversation")
public class ChatConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    // الماكينة اللي المحادثة بتتكلم عنها (اختياري)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "context_machine_id")
    private Machine contextMachine;

    // الـ work order اللي المحادثة بتتكلم عنه (اختياري)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "context_work_order_id")
    private WorkOrder contextWorkOrder;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'OPEN'")
    @Column(name = "status", nullable = false)
    private ChatStatus status = ChatStatus.OPEN;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_message_at")
    private Instant lastMessageAt;
}
