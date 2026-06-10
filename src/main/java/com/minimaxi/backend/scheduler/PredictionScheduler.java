package com.minimaxi.backend.scheduler;

import com.minimaxi.backend.entity.Machine;
import com.minimaxi.backend.entity.Prediction;
import com.minimaxi.backend.enums.MachineStatus;
import com.minimaxi.backend.enums.PredictionSeverity;
import com.minimaxi.backend.enums.IssueType;
import com.minimaxi.backend.repository.MachineRepository;
import com.minimaxi.backend.repository.PredictionRepository;
import com.minimaxi.backend.repository.SensorRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class PredictionScheduler {

    private final MachineRepository machineRepository;
    private final SensorRepository sensorRepository;
    private final PredictionRepository predictionRepository;
    private final RestTemplate restTemplate;

    private static final String AI_URL = "https://predictive-maintenance-ai-5n5m.onrender.com/predict";

    public PredictionScheduler(MachineRepository machineRepository,
                               SensorRepository sensorRepository,
                               PredictionRepository predictionRepository) {
        this.machineRepository = machineRepository;
        this.sensorRepository = sensorRepository;
        this.predictionRepository = predictionRepository;
        this.restTemplate = new RestTemplate();
    }

    @Scheduled(fixedRate = 600000) // كل 10 دقايق
    public void runPredictions() {
        System.out.println("Running AI predictions for all machines...");

        List<Machine> machines = machineRepository.findAll();

        for (Machine machine : machines) {
            try {
                // ✅ جيب الـ readings من الـ Gateway بدل الـ DB
                List<Double> data = getGatewayReadings(machine);

                // ابعت للـ AI
                Map<String, Object> requestBody = Map.of("data", data);
                Map response = restTemplate.postForObject(AI_URL, requestBody, Map.class);

                if (response == null) continue;

                // احفظ الـ prediction
                Prediction prediction = new Prediction();
                prediction.setOrganization(machine.getOrganization());
                prediction.setMachine(machine);
                prediction.setPredictedAt(Instant.now());

                Integer riskLevel = (Integer) response.get("risk_level");
                prediction.setSeverity(mapRiskToSeverity(riskLevel));

                Double confidence = ((Number) response.get("confidence")).doubleValue();
                prediction.setFailureProbability(BigDecimal.valueOf(confidence * 100));

                Double rul = ((Number) response.get("RUL")).doubleValue();
                prediction.setRulCycles(BigDecimal.valueOf(rul));
                prediction.setTtfHours(BigDecimal.valueOf(rul * 24));

                String workOrder = (String) response.get("work_order");
                String problemSensor = (String) response.get("problem_sensor");
                prediction.setExplanation(workOrder + " (Problem: " + problemSensor + ")");
                prediction.setSuggestedIssueType(IssueType.MECHANICAL);

                predictionRepository.save(prediction);
                updateMachineStatus(machine, riskLevel);

                System.out.println(" Prediction saved for: " + machine.getName()
                        + " | Risk: " + riskLevel
                        + " | RUL: " + rul);

            } catch (Exception e) {
                System.err.println("Failed for machine " + machine.getName() + ": " + e.getMessage());
            }
        }
    }

    // ✅ يولد الـ 21 sensor reading من الـ Gateway logic نفسها
    private List<Double> getGatewayReadings(Machine machine) {
        String assetId = machine.getAssetId() != null ? machine.getAssetId() : "MCH-" + machine.getId();
        String type = machine.getMachineType() != null
                ? machine.getMachineType().toLowerCase() : "general";
        String status = machine.getStatus() != null
                ? machine.getStatus().name().toLowerCase() : "healthy";
        String criticality = machine.getCriticality() != null
                ? machine.getCriticality().name().toLowerCase() : "medium";

        double stressFactor = 1.0;
        if ("critical".equals(status))    stressFactor = 1.4;
        else if ("warning".equals(status)) stressFactor = 1.2;
        if ("high".equals(criticality))   stressFactor += 0.1;

        int seedBase = assetId.hashCode();
        long timeSlot = System.currentTimeMillis() / 30000;
        Random r = new Random(seedBase + timeSlot);
        double sf = stressFactor;

        return switch (type) {
            case "cnc machine", "cnc" -> List.of(
                    round(580 + r.nextDouble() * 80 * sf),
                    round(680 + r.nextDouble() * 60 * sf),
                    round(1800 + r.nextDouble() * 200 * sf),
                    round(1500 + r.nextDouble() * 200 * sf),
                    round(28 + r.nextDouble() * 10 * sf),
                    round(42 + r.nextDouble() * 12 * sf),
                    round(680 + r.nextDouble() * 60 * sf),
                    round(2400 + r.nextDouble() * 200 * sf),
                    round(9800 + r.nextDouble() * 800 * sf),
                    round(1.8 + r.nextDouble() * 0.4 * sf),
                    round(85 + r.nextDouble() * 10 * sf),
                    round(600 + r.nextDouble() * 80 * sf),
                    round(2400 + r.nextDouble() * 200 * sf),
                    round(8800 + r.nextDouble() * 800 * sf),
                    round(10.0 + r.nextDouble() * 4 * sf),
                    round(0.10 + r.nextDouble() * 0.05 * sf),
                    round(490 + r.nextDouble() * 40 * sf),
                    round(2450 + r.nextDouble() * 150 * sf),
                    round(82 + r.nextDouble() * 8 * sf),
                    round(48 + r.nextDouble() * 10 * sf),
                    round(38 + r.nextDouble() * 8 * sf)
            );
            case "pump", "hydraulic pump" -> List.of(
                    round(520 + r.nextDouble() * 60 * sf),
                    round(720 + r.nextDouble() * 80 * sf),
                    round(1600 + r.nextDouble() * 300 * sf),
                    round(1400 + r.nextDouble() * 250 * sf),
                    round(25 + r.nextDouble() * 12 * sf),
                    round(38 + r.nextDouble() * 15 * sf),
                    round(640 + r.nextDouble() * 80 * sf),
                    round(2200 + r.nextDouble() * 300 * sf),
                    round(9000 + r.nextDouble() * 1000 * sf),
                    round(1.6 + r.nextDouble() * 0.5 * sf),
                    round(80 + r.nextDouble() * 12 * sf),
                    round(580 + r.nextDouble() * 100 * sf),
                    round(2200 + r.nextDouble() * 300 * sf),
                    round(8200 + r.nextDouble() * 1000 * sf),
                    round(9.0 + r.nextDouble() * 5 * sf),
                    round(0.09 + r.nextDouble() * 0.06 * sf),
                    round(460 + r.nextDouble() * 60 * sf),
                    round(2300 + r.nextDouble() * 200 * sf),
                    round(78 + r.nextDouble() * 10 * sf),
                    round(45 + r.nextDouble() * 12 * sf),
                    round(35 + r.nextDouble() * 10 * sf)
            );
            case "engine", "industrial engine" -> List.of(
                    round(700 + r.nextDouble() * 100 * sf),
                    round(760 + r.nextDouble() * 80 * sf),
                    round(2000 + r.nextDouble() * 300 * sf),
                    round(1700 + r.nextDouble() * 200 * sf),
                    round(38 + r.nextDouble() * 15 * sf),
                    round(55 + r.nextDouble() * 15 * sf),
                    round(760 + r.nextDouble() * 80 * sf),
                    round(2600 + r.nextDouble() * 300 * sf),
                    round(11000 + r.nextDouble() * 1000 * sf),
                    round(2.2 + r.nextDouble() * 0.5 * sf),
                    round(92 + r.nextDouble() * 8 * sf),
                    round(700 + r.nextDouble() * 80 * sf),
                    round(2600 + r.nextDouble() * 300 * sf),
                    round(10000 + r.nextDouble() * 1200 * sf),
                    round(13.0 + r.nextDouble() * 5 * sf),
                    round(0.14 + r.nextDouble() * 0.05 * sf),
                    round(540 + r.nextDouble() * 60 * sf),
                    round(2700 + r.nextDouble() * 200 * sf),
                    round(90 + r.nextDouble() * 8 * sf),
                    round(58 + r.nextDouble() * 10 * sf),
                    round(46 + r.nextDouble() * 8 * sf)
            );
            case "compressor", "air compressor" -> List.of(
                    round(560 + r.nextDouble() * 70 * sf),
                    round(700 + r.nextDouble() * 70 * sf),
                    round(1700 + r.nextDouble() * 250 * sf),
                    round(1550 + r.nextDouble() * 200 * sf),
                    round(30 + r.nextDouble() * 12 * sf),
                    round(44 + r.nextDouble() * 12 * sf),
                    round(700 + r.nextDouble() * 70 * sf),
                    round(2300 + r.nextDouble() * 250 * sf),
                    round(9500 + r.nextDouble() * 900 * sf),
                    round(1.9 + r.nextDouble() * 0.4 * sf),
                    round(87 + r.nextDouble() * 10 * sf),
                    round(620 + r.nextDouble() * 90 * sf),
                    round(2300 + r.nextDouble() * 250 * sf),
                    round(8600 + r.nextDouble() * 900 * sf),
                    round(11.0 + r.nextDouble() * 4 * sf),
                    round(0.11 + r.nextDouble() * 0.05 * sf),
                    round(500 + r.nextDouble() * 50 * sf),
                    round(2400 + r.nextDouble() * 200 * sf),
                    round(84 + r.nextDouble() * 9 * sf),
                    round(50 + r.nextDouble() * 10 * sf),
                    round(40 + r.nextDouble() * 8 * sf)
            );
            default -> List.of(
                    round(600 + r.nextDouble() * 100 * sf),
                    round(700 + r.nextDouble() * 80 * sf),
                    round(1800 + r.nextDouble() * 300 * sf),
                    round(1550 + r.nextDouble() * 200 * sf),
                    round(30 + r.nextDouble() * 12 * sf),
                    round(45 + r.nextDouble() * 12 * sf),
                    round(700 + r.nextDouble() * 80 * sf),
                    round(2400 + r.nextDouble() * 200 * sf),
                    round(9500 + r.nextDouble() * 800 * sf),
                    round(1.9 + r.nextDouble() * 0.4 * sf),
                    round(87 + r.nextDouble() * 8 * sf),
                    round(630 + r.nextDouble() * 80 * sf),
                    round(2400 + r.nextDouble() * 200 * sf),
                    round(8800 + r.nextDouble() * 800 * sf),
                    round(11.0 + r.nextDouble() * 4 * sf),
                    round(0.11 + r.nextDouble() * 0.05 * sf),
                    round(500 + r.nextDouble() * 40 * sf),
                    round(2450 + r.nextDouble() * 150 * sf),
                    round(83 + r.nextDouble() * 8 * sf),
                    round(50 + r.nextDouble() * 10 * sf),
                    round(39 + r.nextDouble() * 8 * sf)
            );
        };
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private PredictionSeverity mapRiskToSeverity(Integer riskLevel) {
        if (riskLevel == null) return PredictionSeverity.LOW;
        return switch (riskLevel) {
            case 0 -> PredictionSeverity.LOW;
            case 1 -> PredictionSeverity.MEDIUM;
            case 2 -> PredictionSeverity.HIGH;
            case 3 -> PredictionSeverity.CRITICAL;
            default -> PredictionSeverity.LOW;
        };
    }

    private void updateMachineStatus(Machine machine, Integer riskLevel) {
        if (riskLevel == null) return;
        MachineStatus newStatus = switch (riskLevel) {
            case 0 -> MachineStatus.HEALTHY;
            case 1 -> MachineStatus.WARNING;
            case 2, 3 -> MachineStatus.CRITICAL;
            default -> MachineStatus.HEALTHY;
        };
        machine.setStatus(newStatus);
        machineRepository.save(machine);
    }
}