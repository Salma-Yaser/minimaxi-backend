package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.dto.request.AddWorkOrderNoteRequest;
import com.minimaxi.backend.dto.request.CreateWorkOrderRequest;
import com.minimaxi.backend.dto.request.UpdateWorkOrderRequest;
import com.minimaxi.backend.dto.response.WorkOrderNoteResponse;
import com.minimaxi.backend.dto.response.WorkOrderResponse;
import com.minimaxi.backend.entity.WorkOrder;
import com.minimaxi.backend.enums.WorkOrderPriority;
import com.minimaxi.backend.enums.WorkOrderStatus;
import com.minimaxi.backend.mapper.WorkOrderMapper;
import com.minimaxi.backend.repository.AppUserRepository;
import com.minimaxi.backend.repository.IssueRepository;
import com.minimaxi.backend.repository.MachineRepository;
import com.minimaxi.backend.repository.OrganizationRepository;
import com.minimaxi.backend.repository.WorkOrderRepository;
import com.minimaxi.backend.service.WorkOrderService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final OrganizationRepository organizationRepository;
    private final MachineRepository machineRepository;
    private final AppUserRepository appUserRepository;
    private final IssueRepository issueRepository;

    public WorkOrderServiceImpl(
            WorkOrderRepository workOrderRepository,
            OrganizationRepository organizationRepository,
            MachineRepository machineRepository,
            AppUserRepository appUserRepository,
            IssueRepository issueRepository
    ) {
        this.workOrderRepository = workOrderRepository;
        this.organizationRepository = organizationRepository;
        this.machineRepository = machineRepository;
        this.appUserRepository = appUserRepository;
        this.issueRepository = issueRepository;
    }

    // ─── GET ALL (مع filters) ────────────────────────────────────────────────

    @Override
    public List<WorkOrderResponse> getAllWorkOrders(String status, String priority, Long assignedTo) {
        return workOrderRepository.findAll()
                .stream()
                .filter(wo -> status == null || status.isBlank() ||
                        wo.getStatus().name().equalsIgnoreCase(status))
                .filter(wo -> priority == null || priority.isBlank() ||
                        wo.getPriority().name().equalsIgnoreCase(priority))
                .filter(wo -> assignedTo == null ||
                        (wo.getAssignedToUser() != null && wo.getAssignedToUser().getId().equals(assignedTo)))
                .map(WorkOrderMapper::toResponse)
                .toList();
    }

    // ─── GET BY ID ───────────────────────────────────────────────────────────

    @Override
    public WorkOrderResponse getWorkOrderById(Long id) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Work order not found with id: " + id));
        return WorkOrderMapper.toResponse(workOrder);
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────

    @Override
    public WorkOrderResponse createWorkOrder(CreateWorkOrderRequest request) {
        WorkOrder workOrder = new WorkOrder();

        workOrder.setOrganization(
                organizationRepository.findById(request.getOrganizationId())
                        .orElseThrow(() -> new RuntimeException("Organization not found"))
        );
        workOrder.setMachine(
                machineRepository.findById(request.getMachineId())
                        .orElseThrow(() -> new RuntimeException("Machine not found"))
        );
        workOrder.setCreatedByUser(
                appUserRepository.findById(request.getCreatedByUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"))
        );

        if (request.getAssignedToUserId() != null) {
            workOrder.setAssignedToUser(
                    appUserRepository.findById(request.getAssignedToUserId())
                            .orElseThrow(() -> new RuntimeException("Assigned user not found"))
            );
        }
        if (request.getIssueId() != null) {
            workOrder.setIssue(
                    issueRepository.findById(request.getIssueId())
                            .orElseThrow(() -> new RuntimeException("Issue not found"))
            );
        }

        workOrder.setTitle(request.getTitle());
        workOrder.setDescription(request.getDescription());
        workOrder.setDueDate(request.getDueDate() != null ? LocalDate.parse(request.getDueDate()) : null);
        workOrder.setAiSuggested(request.getAiSuggested() != null ? request.getAiSuggested() : false);
        workOrder.setCreatedAt(Instant.now());
        workOrder.setPriority(
                request.getPriority() != null
                        ? WorkOrderPriority.valueOf(request.getPriority().toUpperCase())
                        : WorkOrderPriority.MEDIUM
        );
        workOrder.setStatus(
                request.getStatus() != null
                        ? WorkOrderStatus.valueOf(request.getStatus().toUpperCase())
                        : WorkOrderStatus.OPEN
        );

        return WorkOrderMapper.toResponse(workOrderRepository.save(workOrder));
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    @Override
    public WorkOrderResponse updateWorkOrder(Long id, UpdateWorkOrderRequest request) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Work order not found with id: " + id));

        if (request.getTitle() != null)       workOrder.setTitle(request.getTitle());
        if (request.getDescription() != null) workOrder.setDescription(request.getDescription());
        if (request.getDueDate() != null)     workOrder.setDueDate(LocalDate.parse(request.getDueDate()));

        if (request.getPriority() != null) {
            workOrder.setPriority(WorkOrderPriority.valueOf(request.getPriority().toUpperCase()));
        }

        if (request.getStatus() != null) {
            WorkOrderStatus newStatus = WorkOrderStatus.valueOf(request.getStatus().toUpperCase());
            workOrder.setStatus(newStatus);
            // لو اتغير لـ COMPLETED أو CLOSED، نسجل وقت الإغلاق
            if (newStatus == WorkOrderStatus.COMPLETED || newStatus == WorkOrderStatus.CLOSED) {
                workOrder.setClosedAt(Instant.now());
            }
        }

        if (request.getAssignedToUserId() != null) {
            workOrder.setAssignedToUser(
                    appUserRepository.findById(request.getAssignedToUserId())
                            .orElseThrow(() -> new RuntimeException("Assigned user not found"))
            );
        }

        return WorkOrderMapper.toResponse(workOrderRepository.save(workOrder));
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────

    @Override
    public void deleteWorkOrder(Long id) {
        if (!workOrderRepository.existsById(id)) {
            throw new RuntimeException("Work order not found with id: " + id);
        }
        workOrderRepository.deleteById(id);
    }

    // ─── ADD NOTE ────────────────────────────────────────────────────────────
    // الـ schema مش عندها work_order_note table
    // فبنرجع response مباشرة من غير ما نحفظ في DB
    // لو احتجتي تحفظي النوتس، ضيفي table جديدة وعدلي هنا

    @Override
    public WorkOrderNoteResponse addWorkOrderNote(Long id, AddWorkOrderNoteRequest request) {
        // تأكدي إن الـ work order موجود
        if (!workOrderRepository.existsById(id)) {
            throw new RuntimeException("Work order not found with id: " + id);
        }

        return new WorkOrderNoteResponse(
                UUID.randomUUID().toString(),
                request.getContent(),
                request.getUserId(),
                Instant.now().toString()
        );
    }
}