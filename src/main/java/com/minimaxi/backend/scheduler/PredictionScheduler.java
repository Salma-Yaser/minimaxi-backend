package com.minimaxi.backend.scheduler;

import com.minimaxi.backend.entity.Machine;
import com.minimaxi.backend.entity.Prediction;
import com.minimaxi.backend.enums.IssueType;
import com.minimaxi.backend.enums.MachineStatus;
import com.minimaxi.backend.enums.PredictionSeverity;
import com.minimaxi.backend.repository.MachineRepository;
import com.minimaxi.backend.repository.PredictionRepository;
import com.minimaxi.backend.service.SensorGeneratorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
public class PredictionScheduler {

    private final MachineRepository machineRepository;
    private final PredictionRepository predictionRepository;
    private final SensorGeneratorService sensorGenerator;
    private final RestTemplate restTemplate;

    private static final String AI_URL =
            "https://predictive-maintenance-ai-5n5m.onrender.com/predict";

    public PredictionScheduler(MachineRepository machineRepository,
                               PredictionRepository predictionRepository,
                               SensorGeneratorService sensorGenerator) {
        this.machineRepository = machineRepository;
        this.predictionRepository = predictionRepository;
        this.sensorGenerator = sensorGenerator;
        this.restTemplate = new RestTemplate();
    }

    @Scheduled(fixedRate = 600000)
    public void runPredictions() {
        System.out.println("Running AI predictions for all machines...");

        List<Machine> machines = machineRepository.findAll();

        for (Machine machine : machines) {
            try {
                List<Double> data = sensorGenerator.generate(machine);

                Map<String, Object> requestBody = Map.of("data", data);
                Map response = restTemplate.postForObject(AI_URL, requestBody, Map.class);
                if (response == null) continue;

                savePrediction(machine, response);

                System.out.println("Prediction saved for: " + machine.getName());

            } catch (Exception e) {
                System.err.println("Failed for: " + machine.getName() + " - " + e.getMessage());
            }
        }
    }

    private void savePrediction(Machine machine, Map response) {
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