package com.minimaxi.backend.scheduler;

import com.minimaxi.backend.entity.Machine;
import com.minimaxi.backend.entity.Prediction;
import com.minimaxi.backend.entity.Sensor;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

    // كل 10 دقايق
    @Scheduled(fixedRate = 600000)
    public void runPredictions() {
        System.out.println("🤖 Running AI predictions for all machines...");

        List<Machine> machines = machineRepository.findAll();

        for (Machine machine : machines) {
            try {
                // جيب الـ sensor readings الحالية
                List<Sensor> sensors = sensorRepository.findByMachineId(machine.getId());

                if (sensors.isEmpty()) continue;

                // بني الـ data array
                List<Double> data = buildSensorData(sensors);

                if (data.size() < 21) continue;
                // ابعت للـ AI
                Map<String, Object> requestBody = Map.of("data", data);
                Map response = restTemplate.postForObject(AI_URL, requestBody, Map.class);

                if (response == null) continue;

                // احفظ الـ prediction
                Prediction prediction = new Prediction();
                prediction.setOrganization(machine.getOrganization());
                prediction.setMachine(machine);
                prediction.setPredictedAt(Instant.now());

                // risk_level → severity
                Integer riskLevel = (Integer) response.get("risk_level");
                prediction.setSeverity(mapRiskToSeverity(riskLevel));

                // confidence → failure_probability
                Double confidence = ((Number) response.get("confidence")).doubleValue();
                prediction.setFailureProbability(BigDecimal.valueOf(confidence * 100));

                // RUL
                Double rul = ((Number) response.get("RUL")).doubleValue();
                prediction.setRulCycles(BigDecimal.valueOf(rul));
                prediction.setTtfHours(BigDecimal.valueOf(rul * 24));

                // explanation من الـ work_order
                String workOrder = (String) response.get("work_order");
                String problemSensor = (String) response.get("problem_sensor");
                prediction.setExplanation(workOrder + " (Problem: " + problemSensor + ")");
                prediction.setSuggestedIssueType(IssueType.MECHANICAL);

                predictionRepository.save(prediction);

                // حدّث الـ machine status
                updateMachineStatus(machine, riskLevel);

                System.out.println("Prediction saved for machine: " + machine.getName());

            } catch (Exception e) {
                System.err.println("Failed for machine " + machine.getName() + ": " + e.getMessage());
            }
        }
    }

    private List<Double> buildSensorData(List<Sensor> sensors) {
        // بنجيب بس الـ sensors من id 4 لـ 24 بالترتيب
        return sensors.stream()
                .filter(s -> s.getSensorType().getId() >= 1 && s.getSensorType().getId() <= 21)
                .sorted(Comparator.comparing(s -> s.getSensorType().getId()))
                .map(s -> s.getCurrentValue() != null ? s.getCurrentValue().doubleValue() : 0.0)
                .limit(21)
                .collect(java.util.stream.Collectors.toList());
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