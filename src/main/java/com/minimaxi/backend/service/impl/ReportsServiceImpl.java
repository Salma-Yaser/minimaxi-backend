package com.minimaxi.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimaxi.backend.dto.response.ReportsResponse;
import com.minimaxi.backend.entity.Machine;
import com.minimaxi.backend.entity.WorkOrder;
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
import java.time.Instant;
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

    private static final List<String> MONTH_ORDER = List.of(
            "Jan","Feb","Mar","Apr","May","Jun",
            "Jul","Aug","Sep","Oct","Nov","Dec"
    );

    private static final double BEFORE_HOURS_FACTOR = 1.8;
    private static final double BEFORE_COST_FACTOR = 1.6;

    private static final double TOP_MACHINE_WO_WEIGHT       = 0.5;
    private static final double TOP_MACHINE_DOWNTIME_WEIGHT = 0.5;

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

        // ── Monthly Downtime ──────────────────────────────────────────────────
        var closedWorkOrders = allWorkOrders.stream()
                .filter(wo -> wo.getClosedAt() != null
                        && wo.getCreatedAt() != null
                        && (wo.getStatus() == WorkOrderStatus.COMPLETED
                        || wo.getStatus() == WorkOrderStatus.CLOSED))
                .toList();

        Map<String, Double> afterHoursPerMonth = closedWorkOrders.stream()
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

        // ── Accuracy Trend ────────────────────────────────────────────────────
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

        // ── Monthly Cost ──────────────────────────────────────────────────────
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
                    double afterK  = Math.round((afterCostPerMonth.getOrDefault(month, 0.0) / 1000.0) * 100.0) / 100.0;
                    double beforeK = Math.round((afterK * BEFORE_COST_FACTOR) * 100.0) / 100.0;
                    return new ReportsResponse.MonthlyCost(month, beforeK, afterK);
                })
                .collect(Collectors.toList());

        // ── Technician Performance ───────────────────────────────────────────
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

        // ── MTTR / MTBF ───────────────────────────────────────────────────────
        double mttrHours = closedWorkOrders.isEmpty()
                ? 0.0
                : round2(closedWorkOrders.stream()
                .mapToDouble(wo -> Duration.between(wo.getCreatedAt(), wo.getClosedAt()).toMinutes() / 60.0)
                .average().orElse(0.0));

        double mtbfHours = computeMtbfHours(allWorkOrders);

        ReportsResponse.MttrMtbf mttrMtbf = new ReportsResponse.MttrMtbf(mttrHours, mtbfHours);

        // ── Top 5 Problem Machines ───────────────────────────────────────────
        List<ReportsResponse.TopProblemMachine> topProblemMachines =
                computeTopProblemMachines(allWorkOrders, closedWorkOrders);

        // ── Top Spare Parts ───────────────────────────────────────────────────
        List<ReportsResponse.TopSparePart> topSpareParts = computeTopSpareParts(allWorkOrders);

        // ── Top-level KPIs ───────────────────────────────────────────────────
        double avgBeforeHours = monthlyDowntime.stream()
                .mapToDouble(ReportsResponse.MonthlyDowntime::getBeforeHours).average().orElse(0.0);
        double avgAfterHours = monthlyDowntime.stream()
                .mapToDouble(ReportsResponse.MonthlyDowntime::getAfterHours).average().orElse(0.0);
        double downtimeReduction = avgBeforeHours > 0
                ? round1((avgBeforeHours - avgAfterHours) / avgBeforeHours * 100.0)
                : 0.0;

        double predictionAccuracy = 0.0;
        for (int i = accuracyTrend.size() - 1; i >= 0; i--) {
            Double acc = accuracyTrend.get(i).getAccuracy();
            if (acc != null && acc > 0) {
                predictionAccuracy = acc;
                break;
            }
        }

        double costSavings = monthlyCost.stream()
                .mapToDouble(mc -> (mc.getBefore() - mc.getAfter()) * 1000.0)
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
                technicianPerformance,
                mttrMtbf,
                topProblemMachines,
                topSpareParts
        );
    }

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

    /**
     * MTBF: لكل ماكينة بنرتب الـ work orders بتاعتها حسب createdAt، ونحسب
     * الفرق الزمني بين كل work order والتالية ليها. بعدين متوسط كل الفروق
     * دي عبر كل الماكينات اللي عندها أكتر من work order واحدة.
     */
    private double computeMtbfHours(List<WorkOrder> allWorkOrders) {
        Map<Long, List<Instant>> createdAtPerMachine = allWorkOrders.stream()
                .filter(wo -> wo.getMachine() != null && wo.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        wo -> wo.getMachine().getId(),
                        Collectors.mapping(WorkOrder::getCreatedAt, Collectors.toList())
                ));

        List<Double> allGapsHours = new ArrayList<>();

        for (List<Instant> timestamps : createdAtPerMachine.values()) {
            if (timestamps.size() < 2) continue;

            List<Instant> sorted = timestamps.stream().sorted().toList();
            for (int i = 1; i < sorted.size(); i++) {
                double gapHours = Duration.between(sorted.get(i - 1), sorted.get(i)).toMinutes() / 60.0;
                allGapsHours.add(gapHours);
            }
        }

        if (allGapsHours.isEmpty()) return 0.0;

        return round2(allGapsHours.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
    }

    /**
     * بيرتب الماكينات حسب score مركب (50% عدد work orders + 50% downtime)،
     * بعد تطبيع (normalize) القيمتين لنسبة 0-100 بالنسبة لأعلى قيمة بينهم.
     * بيرجع أعلى 5 بس.
     */
    private List<ReportsResponse.TopProblemMachine> computeTopProblemMachines(
            List<WorkOrder> allWorkOrders, List<WorkOrder> closedWorkOrders) {

        Map<Long, Machine> machineById = allWorkOrders.stream()
                .filter(wo -> wo.getMachine() != null)
                .map(WorkOrder::getMachine)
                .collect(Collectors.toMap(Machine::getId, m -> m, (a, b) -> a));

        Map<Long, Long> woCountPerMachine = allWorkOrders.stream()
                .filter(wo -> wo.getMachine() != null)
                .collect(Collectors.groupingBy(wo -> wo.getMachine().getId(), Collectors.counting()));

        Map<Long, Double> downtimePerMachine = closedWorkOrders.stream()
                .filter(wo -> wo.getMachine() != null)
                .collect(Collectors.groupingBy(
                        wo -> wo.getMachine().getId(),
                        Collectors.summingDouble(wo ->
                                Duration.between(wo.getCreatedAt(), wo.getClosedAt()).toMinutes() / 60.0)
                ));

        if (woCountPerMachine.isEmpty()) return List.of();

        long maxWoCount = woCountPerMachine.values().stream().mapToLong(Long::longValue).max().orElse(1);
        double maxDowntime = downtimePerMachine.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);

        List<ReportsResponse.TopProblemMachine> result = new ArrayList<>();

        for (var entry : woCountPerMachine.entrySet()) {
            Long machineId = entry.getKey();
            Machine machine = machineById.get(machineId);
            if (machine == null) continue;

            long woCount = entry.getValue();
            double downtime = downtimePerMachine.getOrDefault(machineId, 0.0);

            double normalizedWo = maxWoCount > 0 ? (woCount * 100.0 / maxWoCount) : 0.0;
            double normalizedDowntime = maxDowntime > 0 ? (downtime * 100.0 / maxDowntime) : 0.0;

            double score = round1(
                    normalizedWo * TOP_MACHINE_WO_WEIGHT + normalizedDowntime * TOP_MACHINE_DOWNTIME_WEIGHT
            );

            result.add(new ReportsResponse.TopProblemMachine(
                    machineId, machine.getName(), woCount, round1(downtime), score));
        }

        return result.stream()
                .sorted(Comparator.comparingDouble(ReportsResponse.TopProblemMachine::getScore).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * بيقرأ كل spare parts JSON من كل الـ WorkOrderCompletion، يجمعهم حسب
     * الاسم (case-insensitive)، ويرتبهم حسب عدد مرات الاستخدام. بيرجع أعلى 5.
     */
    private List<ReportsResponse.TopSparePart> computeTopSpareParts(List<WorkOrder> allWorkOrders) {
        Map<String, Long> usageCountByName = new HashMap<>();
        Map<String, Double> totalCostByName = new HashMap<>();
        Map<String, String> displayNameByKey = new HashMap<>();

        for (WorkOrder wo : allWorkOrders) {
            if (wo.getCompletion() == null) continue;
            String json = wo.getCompletion().getSpareParts();
            if (json == null || json.isBlank()) continue;

            try {
                JsonNode parts = objectMapper.readTree(json);
                if (!parts.isArray()) continue;

                for (JsonNode part : parts) {
                    if (!part.has("name") || part.get("name").isNull()) continue;
                    String rawName = part.get("name").asText().trim();
                    if (rawName.isEmpty()) continue;

                    String key = rawName.toLowerCase();
                    displayNameByKey.putIfAbsent(key, rawName);

                    double cost = part.has("cost") && !part.get("cost").isNull()
                            ? part.get("cost").asDouble(0.0) : 0.0;
                    double qty = part.has("quantity") && !part.get("quantity").isNull()
                            ? part.get("quantity").asDouble(1.0) : 1.0;

                    usageCountByName.merge(key, 1L, Long::sum);
                    totalCostByName.merge(key, cost * qty, Double::sum);
                }
            } catch (Exception ignored) {
                // JSON غير صالح، نتخطاه
            }
        }

        return usageCountByName.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new ReportsResponse.TopSparePart(
                        displayNameByKey.get(e.getKey()),
                        e.getValue(),
                        round2(totalCostByName.getOrDefault(e.getKey(), 0.0))
                ))
                .collect(Collectors.toList());
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}