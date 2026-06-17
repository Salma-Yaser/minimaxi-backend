package com.minimaxi.backend.scheduler;

import com.minimaxi.backend.entity.WorkOrder;
import com.minimaxi.backend.enums.NotificationType;
import com.minimaxi.backend.repository.WorkOrderRepository;
import com.minimaxi.backend.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class EstimatedHoursReminderScheduler {

    private final WorkOrderRepository workOrderRepository;
    private final NotificationService notificationService;

    public EstimatedHoursReminderScheduler(WorkOrderRepository workOrderRepository,
                                           NotificationService notificationService) {
        this.workOrderRepository = workOrderRepository;
        this.notificationService = notificationService;
    }

    // بتشتغل كل 15 دقيقة عشان تلحق الـ checkpoints بدقة
    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void sendEstimatedHoursReminders() {
        List<WorkOrder> workOrders = workOrderRepository.findActiveWorkOrdersWithEstimatedHours();
        Instant now = Instant.now();

        for (WorkOrder wo : workOrders) {
            Instant startTime = wo.getCreatedAt();
            long totalMinutes = wo.getEstimatedHours() * 60L;

            // حساب الـ checkpoints
            Instant at25 = startTime.plus((long)(totalMinutes * 0.25), ChronoUnit.MINUTES);
            Instant at50 = startTime.plus((long)(totalMinutes * 0.50), ChronoUnit.MINUTES);
            Instant at75 = startTime.plus((long)(totalMinutes * 0.75), ChronoUnit.MINUTES);
            Instant at100 = startTime.plus(totalMinutes, ChronoUnit.MINUTES);

            // window الـ check هي 15 دقيقة عشان تتماشى مع الـ fixedRate
            long windowMinutes = 15;

            if (isWithinWindow(now, at25, windowMinutes)) {
                sendReminder(wo, 25, totalMinutes - (long)(totalMinutes * 0.25));
            } else if (isWithinWindow(now, at50, windowMinutes)) {
                sendReminder(wo, 50, totalMinutes - (long)(totalMinutes * 0.50));
            } else if (isWithinWindow(now, at75, windowMinutes)) {
                sendReminder(wo, 75, totalMinutes - (long)(totalMinutes * 0.75));
            } else if (isWithinWindow(now, at100, windowMinutes)) {
                sendOverdueReminder(wo);
            }
        }
    }

    private boolean isWithinWindow(Instant now, Instant checkpoint, long windowMinutes) {
        return !now.isBefore(checkpoint) &&
                now.isBefore(checkpoint.plus(windowMinutes, ChronoUnit.MINUTES));
    }

    private void sendReminder(WorkOrder wo, int percentDone, long minutesLeft) {
        long hoursLeft = minutesLeft / 60;
        long mins = minutesLeft % 60;

        String timeLeft = hoursLeft > 0
                ? hoursLeft + "h " + (mins > 0 ? mins + "m" : "")
                : mins + "m";

        String title = percentDone + "% of estimated time used";
        String message = "Work Order \"" + wo.getTitle() + "\" — " +
                percentDone + "% of the estimated time has passed. " +
                "You have approximately " + timeLeft.trim() + " remaining.";

        notificationService.notifyWorkOrderEvent(
                wo,
                wo.getAssignedToUser(),
                NotificationType.WO_REMINDER,
                title,
                message
        );
    }

    private void sendOverdueReminder(WorkOrder wo) {
        String title = "Estimated Time Exceeded";
        String message = "Work Order \"" + wo.getTitle() + "\" has exceeded its estimated " +
                wo.getEstimatedHours() + " hour(s). Please update the status or complete it.";

        notificationService.notifyWorkOrderEvent(
                wo,
                wo.getAssignedToUser(),
                NotificationType.WO_REMINDER,
                title,
                message
        );
    }
}