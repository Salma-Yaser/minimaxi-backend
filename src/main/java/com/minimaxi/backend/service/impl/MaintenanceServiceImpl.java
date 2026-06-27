package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.dto.response.MaintenanceEventResponse;
import com.minimaxi.backend.entity.Notification;
import com.minimaxi.backend.entity.Prediction;
import com.minimaxi.backend.entity.WorkOrder;
import com.minimaxi.backend.enums.NotificationType;
import com.minimaxi.backend.enums.PredictionSeverity;
import com.minimaxi.backend.enums.WorkOrderStatus;
import com.minimaxi.backend.repository.NotificationRepository;
import com.minimaxi.backend.repository.WorkOrderRepository;
import com.minimaxi.backend.service.MaintenanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




import com.minimaxi.backend.repository.PredictionRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private static final List<WorkOrderStatus> ACTIVE_OR_DONE_STATUSES =
            List.of(WorkOrderStatus.OPEN, WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.COMPLETED);

    // ترتيب ثابت عشان شكل الـ response يبقى متسق دايمًا في الفرونت
    private static final List<String> TYPE_ORDER = List.of("critical", "warning", "scheduled");

    private final WorkOrderRepository workOrderRepository;
    private final NotificationRepository notificationRepository;
    private final PredictionRepository predictionRepository;
    public MaintenanceServiceImpl(WorkOrderRepository workOrderRepository,
                                  NotificationRepository notificationRepository,
                                    PredictionRepository predictionRepository ) {
        this.workOrderRepository = workOrderRepository;
        this.notificationRepository = notificationRepository;
        this.predictionRepository = predictionRepository;
    }

    @Override
    @Transactional
    public List<MaintenanceEventResponse> getMaintenanceEvents(int month, int year, Long organizationId) {

        YearMonth ym = YearMonth.of(year, month);
        LocalDate startOfMonth = ym.atDay(1);
        LocalDate endOfMonth = ym.atEndOfMonth();

        // map: date -> type -> count
        Map<LocalDate, Map<String, Long>> grouped = new TreeMap<>();

        // ── المصدر 1: Work Orders المجدولة ──────────────────────────────
        List<WorkOrder> workOrders = (organizationId != null)
                ? workOrderRepository.findByOrganization_IdAndDueDateBetweenAndStatusIn(
                organizationId, startOfMonth, endOfMonth, ACTIVE_OR_DONE_STATUSES)
                : workOrderRepository.findByDueDateBetweenAndStatusIn(
                startOfMonth, endOfMonth, ACTIVE_OR_DONE_STATUSES);

        for (WorkOrder wo : workOrders) {
            addEvent(grouped, wo.getDueDate(), "scheduled");
        }

        // ── المصدر 2 و 3: Predictions + Sensor Alerts ───────────────────
        List<NotificationType> alertTypes =
                List.of(NotificationType.PREDICTED_FAILURE, NotificationType.SENSOR_ALERT);

        List<Notification> notifications = (organizationId != null)
                ? notificationRepository.findByOrganizationIdAndTypeIn(organizationId, alertTypes)
                : notificationRepository.findByTypeIn(alertTypes);

        for (Notification n : notifications) {
            LocalDate eventDate = resolveAlertDate(n);
            if (eventDate == null || eventDate.isBefore(startOfMonth) || eventDate.isAfter(endOfMonth)) {
                continue; // برّه الشهر المطلوب
            }
            String type = severityToType(n.getSeverity());
            addEvent(grouped, eventDate, type);
        }

        // ── تحويل النتيجة لـ list مرتبة بالتاريخ ثم بترتيب الـ type ثابت ──
        List<MaintenanceEventResponse> result = new ArrayList<>();
        for (Map.Entry<LocalDate, Map<String, Long>> dateEntry : grouped.entrySet()) {
            String dateStr = dateEntry.getKey().toString();
            Map<String, Long> typeCounts = dateEntry.getValue();
            for (String type : TYPE_ORDER) {
                Long count = typeCounts.get(type);
                if (count != null) {
                    result.add(new MaintenanceEventResponse(dateStr, type, count));
                }
            }
        }

        return result;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────

    private void addEvent(Map<LocalDate, Map<String, Long>> grouped, LocalDate date, String type) {
        if (date == null) return;
        grouped.computeIfAbsent(date, k -> new LinkedHashMap<>())
                .merge(type, 1L, Long::sum);
    }

    /**
     * بيحدد تاريخ الـ event من الـ alert/prediction:
     * - PREDICTED_FAILURE: predictedAt + ttfHours (تاريخ الفشل المتوقع الفعلي)
     *   لو مفيش prediction مرتبطة أو مفيش ttfHours، يرجع لـ predictedAt بس، وفي آخر الحالات createdAt.
     * - SENSOR_ALERT (وأي نوع تاني): createdAt (لأنه حدث فعلي حصل دلوقتي، مش متوقع في المستقبل).
     */
    private LocalDate resolveAlertDate(Notification n) {
        if (n.getType() == NotificationType.PREDICTED_FAILURE && n.getPrediction() != null) {
            Prediction p = n.getPrediction();
            if (p.getPredictedAt() != null) {
                if (p.getTtfHours() != null) {
                    long extraSeconds = ttfHoursToSeconds(p.getTtfHours());
                    return p.getPredictedAt().plusSeconds(extraSeconds)
                            .atZone(ZoneOffset.UTC).toLocalDate();
                }
                return p.getPredictedAt().atZone(ZoneOffset.UTC).toLocalDate();
            }
        }
        return n.getCreatedAt() != null
                ? n.getCreatedAt().atZone(ZoneOffset.UTC).toLocalDate()
                : null;
    }

    private long ttfHoursToSeconds(BigDecimal ttfHours) {
        return ttfHours.multiply(BigDecimal.valueOf(3600)).longValue();
    }

    private String severityToType(PredictionSeverity severity) {
        if (severity == null) {
            return "warning"; // افتراضي آمن لو الـ severity مش متسجلة
        }
        return switch (severity) {
            case CRITICAL, HIGH -> "critical";
            case MEDIUM, LOW -> "warning";
        };
    }



    @Override
    public List<Map<String, Object>> getUpcomingMaintenance(Long orgId) {
        // TTF ≤ 2000 وعندها work order active
        List<Prediction> predictions = predictionRepository
                .findLatestPredictionsWithTtfUnder(orgId, BigDecimal.valueOf(2000));

        List<Map<String, Object>> result = new ArrayList<>();

        for (Prediction p : predictions) {
            List<WorkOrder> activeWOs = workOrderRepository
                    .findActiveWorkOrdersForMachine(orgId, p.getMachine().getId());

            if (activeWOs.isEmpty()) continue; // معندهاش WO → مش upcoming

            WorkOrder wo = activeWOs.get(0); // أحدث WO

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("asset_id", p.getMachine().getAssetId());
            item.put("name", p.getMachine().getName());
            String typeName = null;
            try {
                if (p.getMachine().getAssetType() != null) {
                    typeName = p.getMachine().getAssetType().getName();
                }
            } catch (Exception e) {
                typeName = p.getMachine().getMachineType(); // fallback للـ machineType string
            }
            item.put("type", typeName);
            item.put("ttf_hours", p.getTtfHours());
            item.put("priority", severityToPriority(p.getSeverity()));
            item.put("work_order_id", wo.getId());
            item.put("work_order_number", "WO-" + wo.getCreatedAt().atZone(ZoneOffset.UTC).getYear()
                    + "-" + wo.getId());
            result.add(item);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getExpectedMaintenance(Long orgId) {
        // TTF ≤ 2000 وملهاش work order
        List<Long> machineIdsWithWO = workOrderRepository
                .findMachineIdsWithActiveWorkOrders(orgId);

        List<Prediction> predictions = predictionRepository
                .findLatestPredictionsWithTtfUnder(orgId, BigDecimal.valueOf(2000));

        List<Map<String, Object>> result = new ArrayList<>();

        for (Prediction p : predictions) {
            if (machineIdsWithWO.contains(p.getMachine().getId())) continue; // عندها WO → مش expected

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("asset_id", p.getMachine().getAssetId());
            item.put("name", p.getMachine().getName());
            String typeName = null;
            try {
                if (p.getMachine().getAssetType() != null) {
                    typeName = p.getMachine().getAssetType().getName();
                }
            } catch (Exception e) {
                typeName = p.getMachine().getMachineType(); // fallback للـ machineType string
            }
            item.put("type", typeName);
            item.put("location", p.getMachine().getLocation());
            item.put("ttf_hours", p.getTtfHours());
            item.put("risk_level", severityToPriority(p.getSeverity()));
            item.put("notes", p.getExplanation());
            result.add(item);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getLoadForecast(Long orgId, int weeks) {
        LocalDate today = LocalDate.now();

        List<Long> machineIdsWithWO = workOrderRepository
                .findMachineIdsWithActiveWorkOrders(orgId);

        List<Prediction> allPredictions = predictionRepository
                .findLatestPredictionsForOrg(orgId);

        List<Map<String, Object>> forecast = new ArrayList<>();

        for (int i = 0; i < weeks; i++) {
            LocalDate weekStart = today.plusDays((long) i * 7);
            LocalDate weekEnd = weekStart.plusDays(6);

            // Scheduled: work orders due في الأسبوع ده، status مش completed أو cancelled
            long scheduled = workOrderRepository
                    .findByOrganization_IdAndDueDateBetweenAndStatusIn(
                            orgId, weekStart, weekEnd,
                            List.of(WorkOrderStatus.OPEN, WorkOrderStatus.IN_PROGRESS))
                    .size();

            // Predicted: machines بدون WO، الـ TTF ÷ 8 بيوقعها في الأسبوع ده
            long predicted = allPredictions.stream()
                    .filter(p -> !machineIdsWithWO.contains(p.getMachine().getId()))
                    .filter(p -> p.getPredictedAt() != null)
                    .filter(p -> {
                        double daysUntilFailure = p.getTtfHours().doubleValue() / 8.0;
                        LocalDate failureDate = p.getPredictedAt()
                                .atZone(ZoneOffset.UTC).toLocalDate()
                                .plusDays((long) daysUntilFailure);
                        return !failureDate.isBefore(weekStart) && !failureDate.isAfter(weekEnd);
                    })
                    .count();

            Map<String, Object> weekData = new LinkedHashMap<>();
            weekData.put("week", "Week " + (i + 1));
            weekData.put("week_start", weekStart.toString());
            weekData.put("week_end", weekEnd.toString());
            weekData.put("scheduled", scheduled);
            weekData.put("predicted", predicted);
            forecast.add(weekData);
        }

        return forecast;
    }

    // helper
    private String severityToPriority(PredictionSeverity severity) {
        if (severity == null) return "medium";
        return switch (severity) {
            case CRITICAL -> "critical";
            case HIGH -> "high";
            case MEDIUM -> "medium";
            case LOW -> "low";
        };
    }
}