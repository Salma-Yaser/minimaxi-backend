package com.minimaxi.backend.scheduler;

import com.minimaxi.backend.entity.Issue;
import com.minimaxi.backend.entity.Machine;
import com.minimaxi.backend.entity.Prediction;
import com.minimaxi.backend.enums.IssueSource;
import com.minimaxi.backend.enums.IssueStatus;
import com.minimaxi.backend.enums.IssueType;
import com.minimaxi.backend.enums.MachineStatus;
import com.minimaxi.backend.enums.PredictionSeverity;
import com.minimaxi.backend.repository.IssueRepository;
import com.minimaxi.backend.repository.MachineRepository;
import com.minimaxi.backend.repository.PredictionRepository;
import com.minimaxi.backend.service.SensorGeneratorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.minimaxi.backend.service.NotificationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(
        name = "scheduler.prediction.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class PredictionScheduler {

    private final MachineRepository machineRepository;
    private final PredictionRepository predictionRepository;
    private final IssueRepository issueRepository;
    private final SensorGeneratorService sensorGenerator;
    private final NotificationService notificationService;
    private final RestTemplate restTemplate;

    private static final String AI_URL =
            "https://predictive-maintenance-ai-5n5m.onrender.com/predict";

    // الحالات اللي "بتعتبر لسه مفتوحة" — لو موجودة لنفس الماكينة منمنعش إنشاء issue جديدة
    private static final List<IssueStatus> OPEN_STATUSES =
            List.of(IssueStatus.OPEN, IssueStatus.IN_REVIEW);

    public PredictionScheduler(MachineRepository machineRepository,
                               PredictionRepository predictionRepository,
                               IssueRepository issueRepository,
                               SensorGeneratorService sensorGenerator,
                               NotificationService notificationService) {
        this.machineRepository = machineRepository;
        this.predictionRepository = predictionRepository;
        this.issueRepository = issueRepository;
        this.sensorGenerator = sensorGenerator;
        this.notificationService = notificationService;
        this.restTemplate = new RestTemplate();
    }

    @Scheduled(fixedRate = 600000)
    public void runPredictions() {
        System.out.println("Running AI predictions for all machines...");

        List<Machine> machines = machineRepository.findAll();

        for (Machine machine : machines) {
            try {
                List<Double> data = fetchFromGateway(machine);

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

    @SuppressWarnings("unchecked")
    private List<Double> fetchFromGateway(Machine machine) {
        if (machine.getGatewayUrl() == null || machine.getGatewayUrl().isBlank()) {
            return sensorGenerator.generate(machine);
        }

        try {
            Map response = restTemplate.getForObject(machine.getGatewayUrl(), Map.class);
            if (response == null) return sensorGenerator.generate(machine);

            Map<String, Object> aiPayload = (Map<String, Object>) response.get("aiPayload");
            List<Number> rawData = (List<Number>) aiPayload.get("data");

            return rawData.stream()
                    .map(Number::doubleValue)
                    .toList();
        } catch (Exception e) {
            System.err.println("Gateway fetch failed for " + machine.getName() + ", falling back: " + e.getMessage());
            return sensorGenerator.generate(machine);
        }
    }

    @SuppressWarnings("unchecked")
    private void savePrediction(Machine machine, Map response) {
        Prediction prediction = new Prediction();
        prediction.setOrganization(machine.getOrganization());
        prediction.setMachine(machine);
        prediction.setPredictedAt(Instant.now());

        Integer riskLevel = (Integer) response.get("risk_level");
        prediction.setSeverity(mapRiskToSeverity(riskLevel));

        Double confidence = ((Number) response.get("confidence")).doubleValue();
        prediction.setConfidenceScore(BigDecimal.valueOf(confidence * 100));

        Double rul = ((Number) response.get("RUL")).doubleValue();
        prediction.setRulCycles(BigDecimal.valueOf(rul));
        prediction.setTtfHours(BigDecimal.valueOf(rul * 24));

        Map<String, Object> metrics = (Map<String, Object>) response.get("model_metrics");
        if (metrics != null) {
            prediction.setModelAccuracy(BigDecimal.valueOf(((Number) metrics.get("accuracy")).doubleValue()));
            prediction.setModelPrecision(BigDecimal.valueOf(((Number) metrics.get("precision")).doubleValue()));
            prediction.setModelRecall(BigDecimal.valueOf(((Number) metrics.get("recall")).doubleValue()));
            prediction.setModelF1Score(BigDecimal.valueOf(((Number) metrics.get("f1_score")).doubleValue()));
        }

        Object currentVal = response.get("current_value");
        if (currentVal != null)
            prediction.setCurrentValue(BigDecimal.valueOf(((Number) currentVal).doubleValue()));

        Object nMin = response.get("normal_min");
        if (nMin != null)
            prediction.setNormalMin(BigDecimal.valueOf(((Number) nMin).doubleValue()));

        Object nMax = response.get("normal_max");
        if (nMax != null)
            prediction.setNormalMax(BigDecimal.valueOf(((Number) nMax).doubleValue()));



        String workOrder = (String) response.get("work_order");
        String problemSensor = (String) response.get("problem_sensor");
        prediction.setExplanation(workOrder + " (Problem: " + problemSensor + ")");
        prediction.setSuggestedIssueType(IssueType.MECHANICAL);

        predictionRepository.save(prediction);
        updateMachineStatus(machine, riskLevel);

        if (prediction.getSeverity() == PredictionSeverity.HIGH ||
                prediction.getSeverity() == PredictionSeverity.CRITICAL) {
            notificationService.notifyCriticalPrediction(prediction);
        }

        // Auto-create an Issue من الـ HIGH-severity prediction
        if (prediction.getSeverity() == PredictionSeverity.HIGH) {
            createIssueFromPredictionIfNeeded(machine, prediction, response);
        }
    }

    /**
     * بينشئ Issue تلقائية من prediction بدرجة HIGH، إلا لو فيه issue مفتوحة
     * (OPEN أو IN_REVIEW) أصلاً لنفس الماكينة — في الحالة دي منعشل تكرار.
     */
    @SuppressWarnings("unchecked")
    private void createIssueFromPredictionIfNeeded(Machine machine, Prediction prediction, Map response) {
        boolean hasOpenIssue = issueRepository
                .findFirstByMachineIdAndStatusInOrderByCreatedAtDesc(machine.getId(), OPEN_STATUSES)
                .isPresent();

        if (hasOpenIssue) {
            System.out.println("Skipped issue creation for " + machine.getName()
                    + " — an open issue already exists.");
            return;
        }

        Issue issue = new Issue();
        issue.setOrganization(machine.getOrganization());
        issue.setMachine(machine);
        issue.setCreatedByUser(null); // AI-generated، مفيش human creator
        issue.setPrediction(prediction);
        issue.setSource(IssueSource.AI);
        issue.setSeverity(prediction.getSeverity());
        issue.setStatus(IssueStatus.OPEN);
        issue.setCreatedAt(Instant.now());

        String workOrderText = (String) response.get("work_order");
        issue.setSummary(truncate(
                (workOrderText != null && !workOrderText.isBlank())
                        ? workOrderText
                        : "High risk prediction for " + machine.getName(),
                200
        ));

        issue.setDetails(buildDetails(response));

        issueRepository.save(issue);
        System.out.println("Issue auto-created from prediction for: " + machine.getName());
    }

    private String buildDetails(Map response) {
        StringBuilder sb = new StringBuilder();

        Object problemSensor = response.get("problem_sensor");
        if (problemSensor != null) {
            sb.append("Problem sensor: ").append(problemSensor).append("\n");
        }

        Object currentValue = response.get("current_value");
        if (currentValue != null) {
            sb.append("Current value: ").append(currentValue).append("\n");
        }

        Object normalMin = response.get("normal_min");
        Object normalMax = response.get("normal_max");
        if (normalMin != null && normalMax != null) {
            sb.append("Normal range: ").append(normalMin).append(" - ").append(normalMax).append("\n");
        }

        Object rul = response.get("RUL");
        if (rul != null) {
            sb.append("Remaining useful life (cycles): ").append(rul).append("\n");
        }

        Object confidence = response.get("confidence");
        if (confidence != null) {
            double confPct = ((Number) confidence).doubleValue() * 100;
            sb.append("Model confidence: ").append(Math.round(confPct * 10.0) / 10.0).append("%\n");
        }

        return sb.toString().stripTrailing();
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return null;
        return text.length() <= maxLen ? text : text.substring(0, maxLen);
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