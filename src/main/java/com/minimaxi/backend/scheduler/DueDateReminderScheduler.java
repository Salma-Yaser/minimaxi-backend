package com.minimaxi.backend.scheduler;

import com.minimaxi.backend.entity.WorkOrder;
import com.minimaxi.backend.enums.NotificationType;
import com.minimaxi.backend.enums.WorkOrderStatus;
import com.minimaxi.backend.repository.WorkOrderRepository;
import com.minimaxi.backend.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DueDateReminderScheduler {

    private final WorkOrderRepository workOrderRepository;
    private final NotificationService notificationService;

    public DueDateReminderScheduler(WorkOrderRepository workOrderRepository,
                                    NotificationService notificationService) {
        this.workOrderRepository = workOrderRepository;
        this.notificationService = notificationService;
    }

    // كل يوم الساعة 8 الصبح
    @Scheduled(cron = "0 0 8 * * *")
    public void remindDueWorkOrders() {
        LocalDate today = LocalDate.now();

        List<WorkOrder> workOrders = workOrderRepository.findAll();

        for (WorkOrder wo : workOrders) {
            if (wo.getDueDate() == null) continue;
            if (wo.getStatus() != WorkOrderStatus.OPEN && wo.getStatus() != WorkOrderStatus.IN_PROGRESS) continue;
            if (wo.getAssignedToUser() == null) continue;

            if (wo.getDueDate().isEqual(today) || wo.getDueDate().isBefore(today)) {
                String title = wo.getDueDate().isBefore(today) ? "Work Order Overdue" : "Work Order Due Today";
                String message = title + ": " + wo.getTitle();

                notificationService.notifyWorkOrderEvent(
                        wo,
                        wo.getAssignedToUser(),
                        NotificationType.WO_STATUS_CHANGED,
                        title,
                        message
                );
            }
        }
    }
}