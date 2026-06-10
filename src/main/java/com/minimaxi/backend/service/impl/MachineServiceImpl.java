package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.dto.request.CreateMachineRequest;
import com.minimaxi.backend.dto.request.UpdateMachineRequest;
import com.minimaxi.backend.dto.response.MachinePredictionResponse;
import com.minimaxi.backend.dto.response.MachineResponse;
import com.minimaxi.backend.dto.response.SensorHistoryResponse;
import com.minimaxi.backend.entity.Machine;
import com.minimaxi.backend.enums.MachineCriticality;
import com.minimaxi.backend.enums.MachineStatus;
import com.minimaxi.backend.mapper.MachineMapper;
import com.minimaxi.backend.repository.MachineRepository;
import com.minimaxi.backend.repository.OrganizationRepository;
import com.minimaxi.backend.repository.PredictionRepository;
import com.minimaxi.backend.repository.SensorReadingRepository;
import com.minimaxi.backend.repository.SensorRepository;
import com.minimaxi.backend.service.MachineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.minimaxi.backend.entity.Issue;
import com.minimaxi.backend.entity.WorkOrder;
import com.minimaxi.backend.entity.WorkOrderCompletion;
import com.minimaxi.backend.repository.IssueRepository;
import com.minimaxi.backend.repository.WorkOrderRepository;
import com.minimaxi.backend.repository.WorkOrderCompletionRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MachineServiceImpl implements MachineService {

    private final MachineRepository machineRepository;
    private final SensorRepository sensorRepository;
    private final PredictionRepository predictionRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final OrganizationRepository organizationRepository;

    private final IssueRepository issueRepository;
    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderCompletionRepository workOrderCompletionRepository;

    public MachineServiceImpl(
            MachineRepository machineRepository,
            SensorRepository sensorRepository,
            PredictionRepository predictionRepository,
            SensorReadingRepository sensorReadingRepository,
            OrganizationRepository organizationRepository,
            IssueRepository issueRepository,
            WorkOrderRepository workOrderRepository,
            WorkOrderCompletionRepository workOrderCompletionRepository
    ) {
        this.machineRepository = machineRepository;
        this.sensorRepository = sensorRepository;
        this.predictionRepository = predictionRepository;
        this.sensorReadingRepository = sensorReadingRepository;
        this.organizationRepository = organizationRepository;
        this.issueRepository = issueRepository;
        this.workOrderRepository = workOrderRepository;
        this.workOrderCompletionRepository = workOrderCompletionRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<MachineResponse> getAllMachines(Long organizationId, String type, String location, String status, String search) {
        return machineRepository.findAll()
                .stream()
                .filter(m -> organizationId == null ||
                        (m.getOrganization() != null && m.getOrganization().getId().equals(organizationId)))
                .filter(m -> type == null || type.isBlank() ||
                        (m.getMachineType() != null && m.getMachineType().equalsIgnoreCase(type)))
                .filter(m -> location == null || location.isBlank() ||
                        (m.getLocation() != null && m.getLocation().equalsIgnoreCase(location)))
                .filter(m -> status == null || status.isBlank() ||
                        (m.getStatus() != null && m.getStatus().name().equalsIgnoreCase(status)))
                .filter(m -> search == null || search.isBlank() ||
                        m.getName().toLowerCase().contains(search.toLowerCase()) ||
                        (m.getAssetId() != null && m.getAssetId().toLowerCase().contains(search.toLowerCase())))
                .map(m -> getMachineById(m.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public MachineResponse getMachineById(Long id) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine not found"));

        var sensors = sensorRepository.findByMachineId(id)
                .stream()
                .filter(s -> s.getSensorType() != null)
                .collect(Collectors.toMap(
                        s -> s.getSensorType().getName().toLowerCase(),
                        s -> s.getCurrentValue() != null ? s.getCurrentValue().doubleValue() : 0.0,
                        (existing, replacement) -> existing
                ));

        var predictionOpt = predictionRepository.findTopByMachineIdOrderByPredictedAtDesc(id);

        MachinePredictionResponse prediction = predictionOpt.map(p ->
                MachinePredictionResponse.builder()
                        .failure_probability(p.getFailureProbability() != null ? p.getFailureProbability().doubleValue() : 0.0)
                        .rul(p.getRulCycles() != null ? p.getRulCycles().doubleValue() : 0.0)
                        .ttf(p.getTtfHours() != null ? p.getTtfHours() + " hrs" : "N/A")
                        .status(p.getSeverity() != null ? p.getSeverity().name().toLowerCase() : "unknown")
                        .recommendation(p.getExplanation())
                        .build()
        ).orElse(
                MachinePredictionResponse.builder()
                        .failure_probability(0.0)
                        .rul(0.0)
                        .ttf("N/A")
                        .status("healthy")
                        .recommendation("No prediction yet")
                        .build()
        );

        return MachineResponse.builder()
                .id(machine.getId())
                .assetId(machine.getAssetId())
                .name(machine.getName())
                .type(machine.getAssetType() != null ? machine.getAssetType().getName() : machine.getMachineType())
                .location(machine.getLocation())
                .serialNumber(machine.getSerialNumber())
                .manufacturer(null)
                .model(null)
                .installationDate(machine.getInstallationDate() != null ? machine.getInstallationDate().toString() : null)
                .criticality(machine.getCriticality() != null ? machine.getCriticality().name().toLowerCase() : null)
                .status(machine.getStatus() != null ? machine.getStatus().name().toLowerCase() : null)
                .lastMaintenance(null)
                .sensors(sensors)
                .prediction(prediction)
                .build();
    }

    @Override
    public MachineResponse createMachine(CreateMachineRequest request) {
        Machine machine = new Machine();

        machine.setOrganization(
                organizationRepository.findById(request.getOrganizationId())
                        .orElseThrow(() -> new RuntimeException("Organization not found"))
        );

        machine.setName(request.getName());
        machine.setMachineType(request.getType());
        machine.setLocation(request.getLocation());
        machine.setSerialNumber(request.getSerialNumber());
        machine.setCreatedAt(Instant.now());
        machine.setAssetId("MCH-TEMP-" + System.currentTimeMillis());

        machine.setCriticality(
                request.getCriticality() != null
                        ? MachineCriticality.valueOf(request.getCriticality().toUpperCase())
                        : MachineCriticality.MEDIUM
        );

        machine.setStatus(MachineStatus.HEALTHY);

        if (request.getInstallationDate() != null) {
            machine.setInstallationDate(LocalDate.parse(request.getInstallationDate()));
        }

        Machine saved = machineRepository.save(machine);
        saved.setAssetId("MCH-" + saved.getId());
        saved = machineRepository.save(saved);

        return MachineMapper.toResponse(saved);
    }

    @Override
    public MachineResponse updateMachine(Long id, UpdateMachineRequest request) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine not found"));

        if (request.getName() != null)         machine.setName(request.getName());
        if (request.getType() != null)         machine.setMachineType(request.getType());
        if (request.getLocation() != null)     machine.setLocation(request.getLocation());
        if (request.getSerialNumber() != null) machine.setSerialNumber(request.getSerialNumber());

        if (request.getCriticality() != null) {
            machine.setCriticality(MachineCriticality.valueOf(request.getCriticality().toUpperCase()));
        }
        if (request.getStatus() != null) {
            machine.setStatus(MachineStatus.valueOf(request.getStatus().toUpperCase()));
        }

        Machine saved = machineRepository.save(machine);
        return MachineMapper.toResponse(saved);
    }

    @Override
    public void deleteMachine(Long id) {
        if (!machineRepository.existsById(id)) {
            throw new RuntimeException("Machine not found");
        }
        machineRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SensorHistoryResponse> getSensorHistory(Long machineId, Integer hours) {
        Instant cutoffTime = Instant.now().minusSeconds((long) (hours != null ? hours : 24) * 3600);

        var readings = sensorReadingRepository
                .findBySensorMachineIdAndReadingTimeAfterOrderByReadingTimeAsc(machineId, cutoffTime);

        if (readings.isEmpty()) {
            return generateMockSensorHistory(machineId, hours != null ? hours : 24);
        }

        Map<String, Map<String, Double>> grouped = new LinkedHashMap<>();

        for (var r : readings) {
            if (r.getSensor() == null || r.getSensor().getSensorType() == null) continue;

            String timestamp = r.getReadingTime().toString();
            grouped.computeIfAbsent(timestamp, k -> new LinkedHashMap<>());

            String type = r.getSensor().getSensorType().getName().toLowerCase();
            grouped.get(timestamp).put(type, r.getValue());
        }

        return grouped.entrySet().stream()
                .map(e -> new SensorHistoryResponse(e.getKey(), e.getValue()))
                .toList();
    }

    private List<SensorHistoryResponse> generateMockSensorHistory(Long machineId, int hours) {
        var sensors = sensorRepository.findByMachineId(machineId);

        List<String> sensorTypes = sensors.stream()
                .filter(s -> s.getSensorType() != null)
                .map(s -> s.getSensorType().getName().toLowerCase())
                .distinct()
                .toList();

        if (sensorTypes.isEmpty()) {
            sensorTypes = List.of("temperature", "vibration", "pressure");
        }

        List<SensorHistoryResponse> result = new ArrayList<>();
        Instant now = Instant.now();
        int totalPoints = hours * 12;
        java.util.Random random = new java.util.Random(machineId);

        Map<String, Double> baseValues = new LinkedHashMap<>();
        for (String type : sensorTypes) {
            baseValues.put(type, getMockBaseValue(type, random));
        }

        for (int i = totalPoints; i >= 0; i--) {
            Instant ts = now.minusSeconds(i * 300L);
            Map<String, Double> values = new LinkedHashMap<>();

            for (String type : sensorTypes) {
                double base = baseValues.get(type);
                double noise = (random.nextDouble() - 0.5) * getMockNoiseRange(type);
                baseValues.put(type, base + (random.nextDouble() - 0.48) * getMockNoiseRange(type) * 0.1);
                values.put(type, Math.round((base + noise) * 100.0) / 100.0);
            }

            result.add(new SensorHistoryResponse(ts.toString(), values));
        }

        return result;
    }

    private double getMockBaseValue(String sensorType, java.util.Random random) {
        return switch (sensorType) {
            case "temperature" -> 65.0 + random.nextDouble() * 15;
            case "vibration"   -> 1.5 + random.nextDouble() * 2.5;
            case "pressure"    -> 88.0 + random.nextDouble() * 10;
            case "humidity"    -> 45.0 + random.nextDouble() * 20;
            case "current"     -> 10.0 + random.nextDouble() * 5;
            case "voltage"     -> 220.0 + random.nextDouble() * 20;
            default            -> 40.0 + random.nextDouble() * 30;
        };
    }

    private double getMockNoiseRange(String sensorType) {
        return switch (sensorType) {
            case "temperature" -> 3.0;
            case "vibration"   -> 0.5;
            case "pressure"    -> 2.0;
            case "humidity"    -> 3.0;
            case "current"     -> 1.0;
            case "voltage"     -> 5.0;
            default            -> 2.0;
        };
    }
    @Override
    public List<Map<String, Object>> getMachineIssues(Long machineId) {
        return issueRepository.findByMachineIdOrderByCreatedAtDesc(machineId)
                .stream()
                .map(issue -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", issue.getId());
                    map.put("summary", issue.getSummary());
                    map.put("details", issue.getDetails());
                    map.put("severity", issue.getSeverity() != null ? issue.getSeverity().name().toLowerCase() : null);
                    map.put("status", issue.getStatus() != null ? issue.getStatus().name().toLowerCase() : null);
                    map.put("source", issue.getSource() != null ? issue.getSource().name().toLowerCase() : null);
                    map.put("created_at", issue.getCreatedAt() != null ? issue.getCreatedAt().toString() : null);
                    return map;
                })
                .toList();
    }

    @Override
    public List<Map<String, Object>> getMachineWorkOrders(Long machineId) {
        return workOrderRepository.findByMachineIdOrderByCreatedAtDesc(machineId)
                .stream()
                .map(wo -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", wo.getId());
                    map.put("title", wo.getTitle());
                    map.put("description", wo.getDescription());
                    map.put("priority", wo.getPriority() != null ? wo.getPriority().name().toLowerCase() : null);
                    map.put("status", wo.getStatus() != null ? wo.getStatus().name().toLowerCase() : null);
                    map.put("due_date", wo.getDueDate() != null ? wo.getDueDate().toString() : null);
                    map.put("created_at", wo.getCreatedAt() != null ? wo.getCreatedAt().toString() : null);
                    map.put("ai_suggested", wo.getAiSuggested());
                    return map;
                })
                .toList();
    }

    @Override
    public List<Map<String, Object>> getMachineNotes(Long machineId) {
        return workOrderCompletionRepository.findByMachineId(machineId)
                .stream()
                .map(woc -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", woc.getId());
                    map.put("work_order_id", woc.getWorkOrder() != null ? woc.getWorkOrder().getId() : null);
                    map.put("work_order_title", woc.getWorkOrder() != null ? woc.getWorkOrder().getTitle() : null);
                    map.put("action_taken", woc.getActionTaken());
                    map.put("root_cause", woc.getRootCause());
                    map.put("additional_notes", woc.getAdditionalNotes());
                    map.put("time_spent_minutes", woc.getTimeSpentMinutes());
                    map.put("completed_at", woc.getCompletedAt() != null ? woc.getCompletedAt().toString() : null);
                    return map;
                })
                .toList();
    }
}