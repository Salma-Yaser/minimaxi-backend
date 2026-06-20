package com.minimaxi.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimaxi.backend.dto.response.ReportsResponse;
import com.minimaxi.backend.entity.WorkOrderCompletion;
import com.minimaxi.backend.entity.WorkOrderRating;
import com.minimaxi.backend.enums.IssueSource;
import com.minimaxi.backend.enums.IssueStatus;
import com.minimaxi.backend.enums.WorkOrderStatus;
import com.minimaxi.backend.repository.IssueRepository;
import com.minimaxi.backend.repository.PredictionRepository;
import com.minimaxi.backend.repository.WorkOrderRatingRepository;
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
    private final IssueRepository issueRepository;
    private final WorkOrderRatingRepository workOrderRatingRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ترتيب الشهور الصح
    private static final List<String> MONTH_ORDER = List.of(
            "Jan","Feb","Mar","Apr","May","Jun",
            "Jul","Aug","Sep","Oct","Nov","Dec"
    );

    // before_hours مفيش لها بيانات تاريخية حقيقية محفوظة، فبنحطها كنسبة
    // تقديرية من الـ after_hours الفعلي (متفق عليها كـ placeholder مؤقت
    // لحد ما يبقى عندنا داتا تاريخية حقيقية قبل تركيب النظام).
    private static final double BEFORE_HOURS_FACTOR = 1.8;

    // نفس الفكرة لـ monthly_cost: before تقديري كنسبة من الـ after الفعلي
    private static final double BEFORE_COST_FACTOR = 1.6;

    public ReportsServiceImpl(WorkOrderRepository workOrderRepository,
                              PredictionRepository predictionRepository,
                              IssueRepository issueRepository,
                              WorkOrderRatingRepository workOrderRatingRepository) {
        this.workOrderRepository  = workOrderRepository;
        this.predictionRepository = predictionRepository;
        this.issueRepository      = issueRepository;
        this.workOrderRatingRepository = workOrderRatingRepository;
    }

    @Override
    @Transactional
    public ReportsResponse getReportsData(Long organizationId) {

        var allWorkOrders = workOrderRepository.findAll().stream()
                .filter(wo -> organizationId == null ||
                        (wo.getOrganization() != null &&
                                wo.getOrganization().getId().equals(organizationId)))
                .toList();

        var allIssues = issueRepository.findAll().stream()
                .filter(i -> organizationId == null ||
                        (i.getOrganization() != null &&
                                i.getOrganization().getId().equals(organizationId)))
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

        // ── Accuracy Trend — من الـ Issues (مصدرها AI) ───────────────────────
        // "التنبؤ صح" = الـ Issue (source=AI) اتعمللها أي action من اليوزر،
        // يعني خرجت من حالة OPEN (IN_REVIEW أو CONVERTED_TO_WO أو CLOSED) —
        // مهما كان وقت الإجراء. accuracy لكل شهر = نسبة الـ AI issues
        // "المتفاعل معاها" من إجمالي AI issues اتعملت في نفس الشهر (حسب createdAt).
        Map<String, Long> aiIssuesPerMonth = allIssues.stream()
                .filter(i -> i.getSource() == IssueSource.AI && i.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        i -> i.getCreatedAt().atZone(ZoneOffset.UTC).format(monthFmt),
                        Collectors.counting()
                ));

        Map<String, Long> aiIssuesActedOnPerMonth = allIssues.stream()
                .filter(i -> i.getSource() == IssueSource.AI
                        && i.getCreatedAt() != null
                        && i.getStatus() != IssueStatus.OPEN)
                .collect(Collectors.groupingBy(
                        i -> i.getCreatedAt().atZone(ZoneOffset.UTC).format(monthFmt),
                        Collectors.counting()
                ));

        List<ReportsResponse.AccuracyTrend> accuracyTrend = MONTH_ORDER.stream()
                .map(month -> {
                    long tot     = aiIssuesPerMonth.getOrDefault(month, 0L);
                    long correct = aiIssuesActedOnPerMonth.getOrDefault(month, 0L);
                    double acc   = tot > 0
                            ? Math.round((double) correct / tot * 100 * 10.0) / 10.0
                            : 0.0;
                    return new ReportsResponse.AccuracyTrend(month, acc);
                })
                .collect(Collectors.toList());

        // ── Monthly Cost — من تكلفة الـ spare parts الفعلية في الـ WO completions ──
        Map<String, Double> afterCostPerMonth = allWorkOrders.stream()
                .filter(wo -> wo.getCompletion() != null
                        && wo.getCompletion().getCompletedAt() != null)
                .collect(Collectors.groupingBy(
                        wo -> wo.getCompletion().getCompletedAt()
                                .atZone(ZoneOffset.UTC).format(monthFmt),
                        Collectors.summingDouble(wo -> sparePartsCost(wo.getCompletion()))
                ));

        List<ReportsResponse.MonthlyCost> monthlyCost = MONTH_ORDER.stream()
                .map(month -> {
                    // الكلفة بالـ $K زي ما الـ frontend متوقع (58.0 = $58K)
                    double afterK  = Math.round((afterCostPerMonth.getOrDefault(month, 0.0) / 1000.0) * 100.0) / 100.0;
                    double beforeK = Math.round((afterK * BEFORE_COST_FACTOR) * 100.0) / 100.0;
                    return new ReportsResponse.MonthlyCost(month, beforeK, afterK);
                })
                .collect(Collectors.toList());

        // ── Technician Performance — من الـ DB + ratings الحقيقية ───────────
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

        var allRatings = workOrderRatingRepository.findAll().stream()
                .filter(r -> organizationId == null
                        || (r.getTechnicianUser() != null
                        && r.getTechnicianUser().getOrganization() != null
                        && r.getTechnicianUser().getOrganization().getId().equals(organizationId)))
                .toList();

        Map<String, List<Integer>> starsPerTechnician = allRatings.stream()
                .filter(r -> r.getTechnicianUser() != null && r.getStars() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getTechnicianUser().getFullName(),
                        Collectors.mapping(WorkOrderRating::getStars, Collectors.toList())
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

                            List<Integer> stars = starsPerTechnician.getOrDefault(name, List.of());
                            double rating = stars.isEmpty()
                                    ? 0.0
                                    : round2(stars.stream().mapToInt(Integer::intValue).average().orElse(0.0));
                            double successRate = stars.isEmpty()
                                    ? 0.0
                                    : round2(stars.stream().filter(s -> s >= 4).count() * 100.0 / stars.size());

                            return new ReportsResponse.TechnicianPerformance(
                                    name,
                                    count,
                                    round2(avgHours),
                                    rating,
                                    round2(totalHrs),
                                    successRate);
                        })
                        .collect(Collectors.toList());

        if (technicianPerformance.isEmpty()) {
            technicianPerformance = List.of(
                    new ReportsResponse.TechnicianPerformance(
                            "No data", 0L, 0.0, 0.0, 0.0, 0.0));
        }

        // ── Top-level KPIs — ديناميكية من الداتا فوق ─────────────────────────
        double avgBeforeHours = monthlyDowntime.stream()
                .mapToDouble(ReportsResponse.MonthlyDowntime::getBeforeHours).average().orElse(0.0);
        double avgAfterHours = monthlyDowntime.stream()
                .mapToDouble(ReportsResponse.MonthlyDowntime::getAfterHours).average().orElse(0.0);
        double downtimeReduction = avgBeforeHours > 0
                ? round1((avgBeforeHours - avgAfterHours) / avgBeforeHours * 100.0)
                : 0.0;

        // آخر شهر فيه بيانات accuracy فعلية (مش صفر)
        double predictionAccuracy = 0.0;
        for (int i = accuracyTrend.size() - 1; i >= 0; i--) {
            Double acc = accuracyTrend.get(i).getAccuracy();
            if (acc != null && acc > 0) {
                predictionAccuracy = acc;
                break;
            }
        }

        double costSavings = monthlyCost.stream()
                .mapToDouble(mc -> (mc.getBefore() - mc.getAfter()) * 1000.0) // من $K لـ $
                .sum();
        costSavings = Math.round(costSavings * 100.0) / 100.0;

        return new ReportsResponse(
                downtimeReduction,
                predictionAccuracy,
                costSavings,
                preventiveVsReactive,
                monthlyDowntime,
                monthlyCost,
                accuracyTrend,
                technicianPerformance
        );
    }

    /**
     * بيقرأ JSON الخاص بالـ spare parts المخزنة في WorkOrderCompletion.spareParts
     * ويحسب مجموع (cost × quantity). لو الفرونت لسه مبعتش cost (داتا قديمة)،
     * أي قطعة من غير cost بتتحسب كـ 0 بدل ما تكسر الحساب.
     */
    private double sparePartsCost(WorkOrderCompletion completion) {
        String json = completion.getSpareParts();
        if (json == null || json.isBlank()) return 0.0;

        try {
            JsonNode parts = objectMapper.readTree(json);
            if (!parts.isArray()) return 0.0;

            double sum = 0.0;
            for (JsonNode part : parts) {
                double cost = part.has("cost") && !part.get("cost").isNull()
                        ? part.get("cost").asDouble(0.0) : 0.0;
                double qty = part.has("quantity") && !part.get("quantity").isNull()
                        ? part.get("quantity").asDouble(1.0) : 1.0;
                sum += cost * qty;
            }
            return sum;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}