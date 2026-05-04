package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.dto.response.*;
import com.minimaxi.backend.entity.Prediction;
import com.minimaxi.backend.enums.MachineStatus;
import com.minimaxi.backend.enums.PredictionSeverity;
import com.minimaxi.backend.enums.WorkOrderStatus;
import com.minimaxi.backend.repository.*;
import com.minimaxi.backend.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final MachineRepository machineRepository;
    private final WorkOrderRepository workOrderRepository;
    private final PredictionRepository predictionRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final SensorRepository sensorRepository;

    public DashboardServiceImpl(
            MachineRepository machineRepository,
            WorkOrderRepository workOrderRepository,
            PredictionRepository predictionRepository,
            SensorReadingRepository sensorReadingRepository,
            SensorRepository sensorRepository
    ) {
        this.machineRepository = machineRepository;
        this.workOrderRepository = workOrderRepository;
        this.predictionRepository = predictionRepository;
        this.sensorReadingRepository = sensorReadingRepository;
        this.sensorRepository = sensorRepository;
    }

    // ─── STATS ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        var machines = machineRepository.findAll();

        long total    = machines.size();
        long healthy  = machines.stream().filter(m -> m.getStatus() == MachineStatus.HEALTHY).count();
        long warning  = machines.stream().filter(m -> m.getStatus() == MachineStatus.WARNING).count();
        long critical = machines.stream().filter(m -> m.getStatus() == MachineStatus.CRITICAL).count();

        long activeWO = workOrderRepository.findAll().stream()
                .filter(wo -> wo.getStatus() != WorkOrderStatus.COMPLETED
                        && wo.getStatus() != WorkOrderStatus.CLOSED)
                .count();

        long predictedFailures = predictionRepository.findAll().stream()
                .filter(p -> p.getSeverity() == PredictionSeverity.HIGH
                        || p.getSeverity() == PredictionSeverity.CRITICAL)
                .map(p -> p.getMachine().getId())
                .distinct()
                .count();

        double uptime = total > 0
                ? Math.round(((double)(total - critical) / total) * 1000.0) / 10.0
                : 100.0;

        return new DashboardStatsResponse(
                total, healthy, warning, critical,
                activeWO, predictedFailures,
                uptime,
                720.0,
                4.2
        );
    }

    // ─── HEALTH DISTRIBUTION ─────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<HealthDistributionResponse> getHealthDistribution() {
        var machines = machineRepository.findAll();

        long healthy  = machines.stream().filter(m -> m.getStatus() == MachineStatus.HEALTHY).count();
        long warning  = machines.stream().filter(m -> m.getStatus() == MachineStatus.WARNING).count();
        long critical = machines.stream().filter(m -> m.getStatus() == MachineStatus.CRITICAL).count();
        long offline  = machines.stream().filter(m -> m.getStatus() == MachineStatus.OFFLINE).count();

        List<HealthDistributionResponse> result = new ArrayList<>();
        if (healthy  > 0) result.add(new HealthDistributionResponse("Healthy",  healthy,  "#4caf50"));
        if (warning  > 0) result.add(new HealthDistributionResponse("Warning",  warning,  "#ff9800"));
        if (critical > 0) result.add(new HealthDistributionResponse("Critical", critical, "#f44336"));
        if (offline  > 0) result.add(new HealthDistributionResponse("Offline",  offline,  "#9e9e9e"));

        return result;
    }

    // ─── FAILURE TREND ───────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<FailureTrendResponse> getFailureTrend(String period) {
        var predictions = predictionRepository.findAll();

        if (predictions.isEmpty()) {
            return getDefaultFailureTrend(period);
        }

        Map<String, List<Double>> grouped = new LinkedHashMap<>();
        DateTimeFormatter formatter = getFormatterForPeriod(period);

        for (var p : predictions) {
            if (p.getPredictedAt() == null || p.getFailureProbability() == null) continue;

            String key = p.getPredictedAt()
                    .atZone(java.time.ZoneOffset.UTC)
                    .format(formatter);

            grouped.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(p.getFailureProbability().doubleValue());
        }

        if (grouped.isEmpty()) return getDefaultFailureTrend(period);

        List<FailureTrendResponse> result = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            double avg = entry.getValue().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            result.add(new FailureTrendResponse(entry.getKey(), Math.round(avg * 10.0) / 10.0));
        }

        return result;
    }

    private DateTimeFormatter getFormatterForPeriod(String period) {
        return switch (period == null ? "monthly" : period.toLowerCase()) {
            case "daily"  -> DateTimeFormatter.ofPattern("MMM dd");
            case "weekly" -> DateTimeFormatter.ofPattern("'Week' w");
            case "yearly" -> DateTimeFormatter.ofPattern("yyyy");
            default       -> DateTimeFormatter.ofPattern("MMM yy");
        };
    }

    private List<FailureTrendResponse> getDefaultFailureTrend(String period) {
        return List.of(
                new FailureTrendResponse("Jan 26", 10.0),
                new FailureTrendResponse("Feb 26", 8.0)
        );
    }

    // ─── SENSOR TRENDS ───────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<SensorTrendResponse> getSensorTrends() {
        var readings = sensorReadingRepository.findAll();

        if (readings.isEmpty()) {
            return getDefaultSensorTrends();
        }

        Map<String, SensorTrendBuilder> grouped = new LinkedHashMap<>();
        DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (var r : readings) {
            if (r.getReadingTime() == null) continue;

            String timeKey = r.getReadingTime()
                    .atZone(java.time.ZoneOffset.UTC)
                    .toLocalDateTime()
                    .format(hourFormatter);
            grouped.putIfAbsent(timeKey, new SensorTrendBuilder(timeKey));

            String type = r.getSensor().getSensorType().getName().toLowerCase();
            SensorTrendBuilder builder = grouped.get(timeKey);

            switch (type) {
                case "temperature" -> builder.temperature = r.getValue();
                case "vibration"   -> builder.vibration   = r.getValue();
                case "pressure"    -> builder.pressure     = r.getValue();
            }
        }

        return grouped.values().stream()
                .map(b -> new SensorTrendResponse(b.time, b.temperature, b.vibration, b.pressure))
                .toList();
    }

    private static class SensorTrendBuilder {
        String time;
        Double temperature, vibration, pressure;
        SensorTrendBuilder(String time) { this.time = time; }
    }

    private List<SensorTrendResponse> getDefaultSensorTrends() {
        return List.of(
                new SensorTrendResponse("00:00", 70.0, 0.2,  95.0),
                new SensorTrendResponse("04:00", 72.0, 0.22, 96.0),
                new SensorTrendResponse("08:00", 75.0, 0.25, 97.0),
                new SensorTrendResponse("12:00", 78.0, 0.28, 98.0),
                new SensorTrendResponse("16:00", 76.0, 0.26, 97.0),
                new SensorTrendResponse("20:00", 73.0, 0.23, 96.0)
        );
    }

    // ─── AI INSIGHTS ─────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<AIInsightResponse> getAIInsights() {
        return predictionRepository.findAll().stream()
                .sorted(Comparator.comparing(p -> ((Prediction) p).getPredictedAt()).reversed())
                .limit(5)
                .map(p -> new AIInsightResponse(
                        p.getId(),
                        p.getMachine().getId(),
                        p.getMachine().getName(),
                        p.getMachine().getAssetId(),
                        p.getExplanation(),
                        p.getSeverity() != null ? p.getSeverity().name().toLowerCase() : "low",
                        p.getFailureProbability() != null ? p.getFailureProbability().doubleValue() : 0.0
                ))
                .toList();
    }
}