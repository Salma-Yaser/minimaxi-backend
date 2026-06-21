package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.dto.request.*;
import com.minimaxi.backend.dto.response.WorkOrderNoteResponse;
import com.minimaxi.backend.dto.response.WorkOrderResponse;
import com.minimaxi.backend.entity.*;
import com.minimaxi.backend.enums.*;
import com.minimaxi.backend.mapper.WorkOrderMapper;
import com.minimaxi.backend.repository.*;
import com.minimaxi.backend.service.NotificationService;
import com.minimaxi.backend.service.WorkOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimaxi.backend.dto.request.RateWorkOrderRequest;
import com.minimaxi.backend.entity.WorkOrderRating;



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
    private final WorkOrderCompletionRepository workOrderCompletionRepository;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NotificationService notificationService;
    private final WorkOrderRatingRepository workOrderRatingRepository;
    public WorkOrderServiceImpl(
            WorkOrderRepository workOrderRepository,
            OrganizationRepository organizationRepository,
            MachineRepository machineRepository,
            AppUserRepository appUserRepository,
            IssueRepository issueRepository,
            WorkOrderCompletionRepository workOrderCompletionRepository,
            NotificationRepository notificationRepository,
            NotificationService notificationService,
            WorkOrderRatingRepository workOrderRatingRepository
    ) {
        this.workOrderRepository = workOrderRepository;
        this.organizationRepository = organizationRepository;
        this.machineRepository = machineRepository;
        this.appUserRepository = appUserRepository;
        this.issueRepository = issueRepository;
        this.workOrderCompletionRepository = workOrderCompletionRepository;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
        this.workOrderRatingRepository = workOrderRatingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getAllWorkOrders(Long organizationId, String status, String priority, Long assignedTo) {
        return workOrderRepository.findAll()
                .stream()
                .filter(wo -> organizationId == null ||
                        (wo.getOrganization() != null && wo.getOrganization().getId().equals(organizationId)))
                .filter(wo -> status == null || status.isBlank() ||
                        wo.getStatus().name().equalsIgnoreCase(status))
                .filter(wo -> priority == null || priority.isBlank() ||
                        wo.getPriority().name().equalsIgnoreCase(priority))
                .filter(wo -> assignedTo == null ||
                        (wo.getAssignedToUser() != null && wo.getAssignedToUser().getId().equals(assignedTo)))
                .map(WorkOrderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkOrderResponse getWorkOrderById(Long id) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Work order not found with id: " + id));
        boolean isRated = workOrderRatingRepository.findByWorkOrderId(id).isPresent();
        return WorkOrderMapper.toResponse(workOrder, isRated);
    }

    @Override
    @Transactional
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

        workOrder.setEstimatedHours(request.getEstimatedHours());
        WorkOrder saved = workOrderRepository.save(workOrder);

        if (saved.getAssignedToUser() != null) {
            notificationService.notifyWorkOrderEvent(
                    saved,
                    saved.getAssignedToUser(),
                    NotificationType.NEW_WORK_ORDER,
                    "New Work Order Assigned",
                    "New work order assigned: " + saved.getTitle()
            );
        }

        return WorkOrderMapper.toResponse(saved);
    }

    @Override
    @Transactional
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
            WorkOrderStatus oldStatus = workOrder.getStatus();
            WorkOrderStatus newStatus = WorkOrderStatus.valueOf(request.getStatus().toUpperCase());
            workOrder.setStatus(newStatus);

            if (newStatus == WorkOrderStatus.COMPLETED || newStatus == WorkOrderStatus.CLOSED || newStatus == WorkOrderStatus.CANCELLED) {
                workOrder.setClosedAt(Instant.now());
            }

            if (newStatus == WorkOrderStatus.CANCELLED) {
                notificationService.notifyWorkOrderEvent(
                        workOrder,
                        workOrder.getAssignedToUser(),
                        NotificationType.WO_STATUS_CHANGED,
                        "Work Order Cancelled",
                        "Work order cancelled: " + workOrder.getTitle()
                );
            }

            if (oldStatus == WorkOrderStatus.OPEN && newStatus == WorkOrderStatus.IN_PROGRESS) {
                notificationService.notifyWorkOrderEvent(
                        workOrder,
                        workOrder.getCreatedByUser(),
                        NotificationType.WO_STATUS_CHANGED,
                        "Work Order Started",
                        "Technician started work on: " + workOrder.getTitle()
                );
            }
        }
        if (request.getEstimatedHours() != null) workOrder.setEstimatedHours(request.getEstimatedHours());

        if (request.getAssignedToUserId() != null) {
            AppUser oldAssignee = workOrder.getAssignedToUser();
            AppUser newAssignee = appUserRepository.findById(request.getAssignedToUserId())
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));

            workOrder.setAssignedToUser(newAssignee);

            boolean isReassignment = oldAssignee != null && !oldAssignee.getId().equals(newAssignee.getId());

            if (isReassignment) {
                notificationService.notifyWorkOrderEvent(
                        workOrder,
                        oldAssignee,
                        NotificationType.WO_STATUS_CHANGED,
                        "Work Order Reassigned",
                        "Work order \"" + workOrder.getTitle() + "\" has been reassigned to another technician."
                );
            }

            if (oldAssignee == null || isReassignment) {
                notificationService.notifyWorkOrderEvent(
                        workOrder,
                        newAssignee,
                        NotificationType.NEW_WORK_ORDER,
                        "Work Order Assigned",
                        "Work order assigned to you: " + workOrder.getTitle()
                );
            }
        }
        return WorkOrderMapper.toResponse(workOrderRepository.save(workOrder));
    }
    @Override
    @Transactional
    public void deleteWorkOrder(Long id) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Work order not found with id: " + id));

        notificationRepository.deleteAll(
                notificationRepository.findAll().stream()
                        .filter(n -> n.getWorkOrder() != null && n.getWorkOrder().getId().equals(id))
                        .toList()
        );

        try {
            workOrderRepository.delete(workOrder);
            workOrderRepository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new IllegalStateException("Cannot delete work order with id " + id +
                    " because it has related records. Please delete related notes and completions first.");


        }
    }

    @Override
    public WorkOrderNoteResponse addWorkOrderNote(Long id, AddWorkOrderNoteRequest request) {
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

    @Override
    @Transactional
    public void completeWorkOrder(Long workOrderId, CompleteWorkOrderRequest request) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("Work order not found with id: " + workOrderId));

        // حساب total minutes
        int totalMinutes = 0;
        if (request.getHoursSpent() != null) totalMinutes += request.getHoursSpent() * 60;
        if (request.getMinutesSpent() != null) totalMinutes += request.getMinutesSpent();

        // إنشاء الـ completion record
        WorkOrderCompletion completion = new WorkOrderCompletion();
        completion.setWorkOrder(workOrder);
        completion.setCompletedByUser(
                appUserRepository.findById(request.getCompletedByUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"))
        );
        completion.setActionTaken(request.getActionTaken());
        completion.setRootCause(request.getRootCause());
        completion.setTimeSpentMinutes(totalMinutes);
        completion.setAdditionalNotes(request.getAdditionalNotes());
        completion.setCompletedAt(Instant.now());

        // spare parts كـ سطور في جدول منفصل (بدل النص الواحد القديم)
        if (request.getSpareParts() != null && !request.getSpareParts().isEmpty()) {
            for (CompleteWorkOrderRequest.SparePart sp : request.getSpareParts()) {
                WorkOrderSparePart entity = new WorkOrderSparePart();
                entity.setCompletion(completion);
                entity.setPartName(sp.getName());
                entity.setQuantity(sp.getQuantity() != null ? sp.getQuantity() : 1);
                completion.getSparePartsList().add(entity);
            }
        }

        workOrderCompletionRepository.save(completion);

        // تحديث الـ work order status
        workOrder.setStatus(WorkOrderStatus.COMPLETED);
        workOrder.setClosedAt(Instant.now());

        workOrderRepository.save(workOrder);

        workOrder.setStatus(WorkOrderStatus.COMPLETED);
        workOrder.setClosedAt(Instant.now());
        workOrderRepository.save(workOrder);

        notificationService.notifyWorkOrderEvent(
                workOrder,
                workOrder.getCreatedByUser(),
                NotificationType.WO_STATUS_CHANGED,
                "Work Order Completed — Please Rate",
                "Work order completed: " + workOrder.getTitle()
                        + ". Please rate the technician's performance."
        );
    }
    @Override
    @Transactional
    public WorkOrderResponse convertIssueToWorkOrder(Long issueId, ConvertIssueToWorkOrderRequest request) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        if (issue.getStatus() == IssueStatus.CONVERTED_TO_WO) {
            throw new RuntimeException("Issue is already converted to a work order");
        }

        WorkOrder workOrder = new WorkOrder();
        workOrder.setOrganization(issue.getOrganization());
        workOrder.setMachine(issue.getMachine());
        workOrder.setIssue(issue);

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

        // العنوان والوصف بييجوا تلقائياً من الـ Issue
        // ملحوظة: WorkOrder.title أقصى طول 150 حرف، بينما Issue.summary لحد 200،
        // فبنعمل truncate دفاعي عشان منقعش في validation error
        String title = issue.getSummary();
        workOrder.setTitle(title != null && title.length() > 150 ? title.substring(0, 150) : title);
        workOrder.setDescription(issue.getDetails());

        workOrder.setDueDate(request.getDueDate() != null ? LocalDate.parse(request.getDueDate()) : null);
        workOrder.setEstimatedHours(request.getEstimatedHours());

        // الجزء الأهم: aiSuggested بيتبني تلقائياً من مصدر الـ Issue، مش من الفرونت
        workOrder.setAiSuggested(issue.getSource() == IssueSource.AI);

        workOrder.setCreatedAt(Instant.now());
        workOrder.setPriority(
                request.getPriority() != null
                        ? WorkOrderPriority.valueOf(request.getPriority().toUpperCase())
                        : WorkOrderPriority.MEDIUM
        );
        workOrder.setStatus(WorkOrderStatus.OPEN);

        WorkOrder saved = workOrderRepository.save(workOrder);

        // قفل الـ Issue أوتوماتيكياً عشان منعملش work order تاني منها بالغلط
        issue.setStatus(IssueStatus.CONVERTED_TO_WO);
        issueRepository.save(issue);

        if (saved.getAssignedToUser() != null) {
            notificationService.notifyWorkOrderEvent(
                    saved,
                    saved.getAssignedToUser(),
                    NotificationType.NEW_WORK_ORDER,
                    "New Work Order Assigned",
                    "New work order assigned: " + saved.getTitle()
            );
        }

        return WorkOrderMapper.toResponse(saved);
    }


    @Override
    @Transactional
    public void rateWorkOrder(Long workOrderId, RateWorkOrderRequest request) {

        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("Work order not found"));

        if (workOrder.getStatus() != WorkOrderStatus.COMPLETED) {
            throw new RuntimeException("Only completed work orders can be rated");
        }

        if (workOrder.getAssignedToUser() == null) {
            throw new RuntimeException("Work order has no assigned technician to rate");
        }

        if (workOrderRatingRepository.findByWorkOrderId(workOrderId).isPresent()) {
            throw new RuntimeException("This work order has already been rated");
        }

        if (request.getStars() == null || request.getStars() < 1 || request.getStars() > 5) {
            throw new RuntimeException("Stars must be between 1 and 5");
        }

        WorkOrderRating rating = new WorkOrderRating();
        rating.setWorkOrder(workOrder);
        rating.setRatedByUser(
                appUserRepository.findById(request.getRatedByUserId())
                        .orElseThrow(() -> new RuntimeException("Rater user not found"))
        );
        rating.setTechnicianUser(workOrder.getAssignedToUser());
        rating.setStars(request.getStars());
        rating.setFeedback(request.getFeedback());
        rating.setCreatedAt(Instant.now());

        workOrderRatingRepository.save(rating);
    }


}