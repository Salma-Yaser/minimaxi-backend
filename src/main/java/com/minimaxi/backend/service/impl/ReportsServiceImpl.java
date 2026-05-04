package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.dto.response.ReportsResponse;
import com.minimaxi.backend.enums.WorkOrderStatus;
import com.minimaxi.backend.repository.WorkOrderRepository;
import com.minimaxi.backend.repository.PredictionRepository;
import com.minimaxi.backend.service.ReportsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportsServiceImpl implements ReportsService {

    private final WorkOrderRepository workOrderRepository;
    private final PredictionRepository predictionRepository;

    public ReportsServiceImpl(WorkOrderRepository workOrderRepository,
                              PredictionRepository predictionRepository) {
        this.workOrderRepository = workOrderRepository;
        this.predictionRepository = predictionRepository;
    }

    @Override
    @Transactional
    public ReportsResponse getReportsData() {
        var allWorkOrders = workOrderRepository.findAll();

        // ─── Completed vs total ───────────────────────────────────────────────
        long total     = allWorkOrders.size();
        long completed = allWorkOrders.stream()
                .filter(wo -> wo.getStatus() == WorkOrderStatus.COMPLETED
                        || wo.getStatus() == WorkOrderStatus.CLOSED)
                .count();

        // ─── Preventive vs Reactive ───────────────────────────────────────────
        long aiSuggested = allWorkOrders.stream()
                .filter(wo -> Boolean.TRUE.equals(wo.getAiSuggested()))
                .count();
        long manual = total - aiSuggested;

        int preventivePct = total > 0 ? (int) Math.round((double) aiSuggested / total * 100) : 75;
        int reactivePct   = 100 - preventivePct;

        Map<String, Integer> preventiveVsReactive = new LinkedHashMap<>();
        preventiveVsReactive.put("preventive", preventivePct);
        preventiveVsReactive.put("reactive",   reactivePct);

        // ─── Monthly downtime (grouped by month from closed work orders) ──────
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMM");
        Map<String, Double> monthlyMap = new LinkedHashMap<>();

        allWorkOrders.stream()
                .filter(wo -> wo.getClosedAt() != null)
                .forEach(wo -> {
                    String month = wo.getClosedAt()
                            .atZone(ZoneOffset.UTC)
                            .format(monthFmt);
                    // نحسب عدد الـ work orders كـ proxy للـ downtime hours
                    monthlyMap.merge(month, 2.0, Double::sum);
                });

        // لو مفيش data نرجع default
        if (monthlyMap.isEmpty()) {
            monthlyMap.put("Jan", 22.0);
            monthlyMap.put("Feb", 18.0);
        }

        List<ReportsResponse.MonthlyDowntime> monthlyDowntime = monthlyMap.entrySet().stream()
                .map(e -> new ReportsResponse.MonthlyDowntime(e.getKey(), e.getValue()))
                .toList();

        // ─── Technician Performance ───────────────────────────────────────────
        Map<String, Long> technicianCompletedMap = allWorkOrders.stream()
                .filter(wo -> wo.getStatus() == WorkOrderStatus.COMPLETED
                        && wo.getAssignedToUser() != null)
                .collect(Collectors.groupingBy(
                        wo -> wo.getAssignedToUser().getFullName(),
                        Collectors.counting()
                ));

        List<ReportsResponse.TechnicianPerformance> technicianPerformance =
                technicianCompletedMap.entrySet().stream()
                        .map(e -> new ReportsResponse.TechnicianPerformance(
                                e.getKey(),
                                e.getValue(),
                                3.5,   // avg_time placeholder
                                4.5    // rating placeholder
                        ))
                        .toList();

        // لو مفيش data نرجع default
        if (technicianPerformance.isEmpty()) {
            technicianPerformance = List.of(
                    new ReportsResponse.TechnicianPerformance("No data", 0, 0.0, 0.0)
            );
        }

        return new ReportsResponse(
                20.0,   // downtime_reduction % — placeholder
                95.0,   // prediction_accuracy % — placeholder
                150000, // cost_savings — placeholder
                preventiveVsReactive,
                monthlyDowntime,
                technicianPerformance
        );
    }
}