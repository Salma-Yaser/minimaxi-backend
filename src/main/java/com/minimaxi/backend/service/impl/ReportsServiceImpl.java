package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.dto.response.ReportsResponse;
import com.minimaxi.backend.entity.WorkOrder;
import com.minimaxi.backend.enums.PredictionSeverity;
import com.minimaxi.backend.enums.WorkOrderStatus;
import com.minimaxi.backend.repository.PredictionRepository;
import com.minimaxi.backend.repository.WorkOrderRepository;
import com.minimaxi.backend.service.ReportsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportsServiceImpl implements ReportsService {

    private final WorkOrderRepository workOrderRepository;
    private final PredictionRepository predictionRepository;

    // ترتيب الشهور الصح
    private static final List<String> MONTH_ORDER = List.of(
            "Jan","Feb","Mar","Apr","May","Jun",
            "Jul","Aug","Sep","Oct","Nov","Dec"
    );

    // before_hours مفيش لها بيانات تاريخية حقيقية محفوظة، فبنحطها كنسبة
    // تقديرية من الـ after_hours الفعلي (متفق عليها كـ placeholder مؤقت
    // لحد ما يبقى عندنا داتا تاريخية حقيقية قبل تركيب النظام).
    private static final double BEFORE_HOURS_FACTOR = 1.8;

    public ReportsServiceImpl(WorkOrderRepository workOrderRepository,
                              PredictionRepository predictionRepository) {
        this.workOrderRepository  = workOrderRepository;
        this.predictionRepository = predictionRepository;
    }

    @Override
    @Transactional
    public ReportsResponse getReportsData(Long organizationId) {

        var allWorkOrders = workOrderRepository.findAll().stream()
                .filter(wo -> organizationId == null ||
                        (wo.getOrganization() != null &&
                                wo.getOrganization().getId().equals(organizationId)))
                .toList();

        var allPredictions = predictionRepository.findAll().stream()
                .filter(p -> organizationId == null ||
                        (p.getOrganization() != null &&
                                p.getOrganization().getId().equals(organizationId)))
                .toList();

        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMM")
                .withLocale(java.util.Locale.ENGLISH);

        // ── Preventive vs Reactive ────────────────────────────────────────────
        long total       = allWorkOrders.size();
        long aiSuggested = allWorkOrders.stream()
                .filter(wo -> Boolean.TRUE.equals(wo.getAiSuggested()))
                .count();
        int preventivePct = total > 0
                ? (int) Math.round((double) aiSuggested / total * 100)
                : 75;

        Map<String, Integer> preventiveVsReactive = new LinkedHashMap<>();
        preventiveVsReactive.put("preventive", preventivePct);
        preventiveVsReactive.put("reactive",   100 - preventivePct);

        // ── Monthly Downtime — من الـ Work Orders الفعلية المقفولة ──────────
        // after_hours لكل شهر = مجموع (closedAt - createdAt) بالساعات لكل
        // work order اتقفلت في الشهر ده (مبني على closedAt، مش createdAt،
        // عشان نعرف فعلياً امتى الماكينة رجعت تشتغل).
        // before_hours = تقديري (factor ثابت) لحد ما يبقى عندنا داتا تاريخية حقيقية.
        Map<String, Double> afterHoursPerMonth = allWorkOrders.stream()
                .filter(wo -> wo.getClosedAt() != null
                        && wo.getCreatedAt() != null
                        && (wo.getStatus() == WorkOrderStatus.COMPLETED
                        || wo.getStatus() == WorkOrderStatus.CLOSED))
                .collect(Collectors.groupingBy(
                        wo -> wo.getClosedAt().atZone(ZoneOffset.UTC).format(monthFmt),
                        Collectors.summingDouble(wo ->
                                Duration.between(wo.getCreatedAt(), wo.getClosedAt()).toMinutes() / 60.0)
                ));

        List<ReportsResponse.MonthlyDowntime> monthlyDowntime = MONTH_ORDER.stream()
                .map(month -> {
                    double after  = Math.round(afterHoursPerMonth.getOrDefault(month, 0.0) * 10.0) / 10.0;
                    double before = Math.round(after * BEFORE_HOURS_FACTOR * 10.0) / 10.0;
                    return new ReportsResponse.MonthlyDowntime(month, before, after);
                })
                .collect(Collectors.toList());

        // ملحوظة: monthlyDowntime دلوقتي بيرجع دايماً 12 عنصر (شهر، حتى لو
        // القيمة صفر)، فمحتاجناش fallback list فاضية بعد كده.

        // ── Accuracy Trend — من الـ predictions (نسبة HIGH+MEDIUM كل شهر) ────
        // نحسب accuracy = (HIGH + MEDIUM) / total predictions لكل شهر * 100
        Map<String, Long> totalPerMonth = allPredictions.stream()
                .filter(p -> p.getSeverity() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getPredictedAt()
                                .atZone(ZoneOffset.UTC)
                                .format(monthFmt),
                        Collectors.counting()
                ));

        Map<String, Long> correctPerMonth = allPredictions.stream()
                .filter(p -> p.getSeverity() == PredictionSeverity.HIGH
                        || p.getSeverity() == PredictionSeverity.MEDIUM)
                .collect(Collectors.groupingBy(
                        p -> p.getPredictedAt()
                                .atZone(ZoneOffset.UTC)
                                .format(monthFmt),
                        Collectors.counting()
                ));

        List<ReportsResponse.AccuracyTrend> accuracyTrend = MONTH_ORDER.stream()
                .map(month -> {
                    long tot     = totalPerMonth.getOrDefault(month, 0L);
                    long correct = correctPerMonth.getOrDefault(month, 0L);
                    double acc   = tot > 0
                            ? Math.round((double) correct / tot * 100 * 10.0) / 10.0
                            : 0.0;
                    return new ReportsResponse.AccuracyTrend(month, acc);
                })
                .collect(Collectors.toList());

        // ملحوظة: accuracyTrend دلوقتي بيرجع دايماً 12 عنصر، فمحتاجناش
        // fallback list فاضية بعد كده.

        // ── Monthly Cost — static (لسه مش متحسوبة من الداتا، انتظار قرار
        //    بخصوص مصدر الـ cost الحقيقي). موسعة لـ 12 شهر عشان الجراف
        //    يفضل متناسق زي باقي الـ charts.
        List<ReportsResponse.MonthlyCost> monthlyCost = List.of(
                new ReportsResponse.MonthlyCost("Jan", 58.0, 45.0),
                new ReportsResponse.MonthlyCost("Feb", 62.0, 42.0),
                new ReportsResponse.MonthlyCost("Mar", 60.0, 40.0),
                new ReportsResponse.MonthlyCost("Apr", 55.0, 38.0),
                new ReportsResponse.MonthlyCost("May", 53.0, 36.0),
                new ReportsResponse.MonthlyCost("Jun", 50.0, 34.0),
                new ReportsResponse.MonthlyCost("Jul", 50.0, 34.0),
                new ReportsResponse.MonthlyCost("Aug", 50.0, 34.0),
                new ReportsResponse.MonthlyCost("Sep", 50.0, 34.0),
                new ReportsResponse.MonthlyCost("Oct", 50.0, 34.0),
                new ReportsResponse.MonthlyCost("Nov", 50.0, 34.0),
                new ReportsResponse.MonthlyCost("Dec", 50.0, 34.0)
        );

        // ── Technician Performance — من الـ DB ───────────────────────────────
        Map<String, List<Long>> techMinutesMap = allWorkOrders.stream()
                .filter(wo -> wo.getStatus() == WorkOrderStatus.COMPLETED
                        && wo.getAssignedToUser() != null
                        && wo.getCompletion() != null)
                .collect(Collectors.groupingBy(
                        wo -> wo.getAssignedToUser().getFullName(),
                        Collectors.mapping(
                                wo -> (long) wo.getCompletion().getTimeSpentMinutes(),
                                Collectors.toList()
                        )
                ));

        List<ReportsResponse.TechnicianPerformance> technicianPerformance =
                techMinutesMap.entrySet().stream()
                        .map(e -> {
                            String name     = e.getKey();
                            List<Long> mins = e.getValue();
                            long count      = mins.size();
                            double avgHours = mins.stream()
                                    .mapToLong(Long::longValue)
                                    .average()
                                    .orElse(210) / 60.0;
                            double totalHrs = mins.stream()
                                    .mapToLong(Long::longValue).sum() / 60.0;
                            return new ReportsResponse.TechnicianPerformance(
                                    name,
                                    count,
                                    round2(avgHours),
                                    4.5,
                                    round2(totalHrs),
                                    94.0);
                        })
                        .collect(Collectors.toList());

        if (technicianPerformance.isEmpty()) {
            technicianPerformance = List.of(
                    new ReportsResponse.TechnicianPerformance(
                            "No data", 0L, 0.0, 0.0, 0.0, 0.0));
        }

        return new ReportsResponse(
                20.0,
                95.0,
                150000.0,
                preventiveVsReactive,
                monthlyDowntime,
                monthlyCost,
                accuracyTrend,
                technicianPerformance
        );
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}