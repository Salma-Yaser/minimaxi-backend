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

import java.time.Instant;
import java.time.LocalDate;
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

    public MachineServiceImpl(
            MachineRepository machineRepository,
            SensorRepository sensorRepository,
            PredictionRepository predictionRepository,
            SensorReadingRepository sensorReadingRepository,
            OrganizationRepository organizationRepository
    ) {
        this.machineRepository = machineRepository;
        this.sensorRepository = sensorRepository;
        this.predictionRepository = predictionRepository;
        this.sensorReadingRepository = sensorReadingRepository;
        this.organizationRepository = organizationRepository;
    }

    // ─── GET ALL (مع filters + organizationId) ───────────────────────────────

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
                .map(MachineMapper::toResponse)
                .toList();
    }

    // ─── GET BY ID ───────────────────────────────────────────────────────────

    @Override
    public MachineResponse getMachineById(Long id) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine not found"));

        var sensors = sensorRepository.findByMachineId(id)
                .stream()
                .collect(Collectors.toMap(
                        s -> s.getSensorType().getName().toLowerCase(),
                        s -> s.getCurrentValue() != null ? s.getCurrentValue().doubleValue() : 0.0
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

    // ─── CREATE ──────────────────────────────────────────────────────────────

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

    // ─── UPDATE ──────────────────────────────────────────────────────────────

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

    // ─── DELETE ──────────────────────────────────────────────────────────────

    @Override
    public void deleteMachine(Long id) {
        if (!machineRepository.existsById(id)) {
            throw new RuntimeException("Machine not found");
        }
        machineRepository.deleteById(id);
    }

    // ─── SENSOR HISTORY ──────────────────────────────────────────────────────

    @Override
    public List<SensorHistoryResponse> getSensorHistory(Long machineId, Integer hours) {
        var readings = sensorReadingRepository
                .findBySensorMachineIdOrderByReadingTimeDesc(machineId);

        Instant cutoffTime = hours != null
                ? Instant.now().minusSeconds((long) hours * 3600)
                : Instant.EPOCH;

        Map<String, SensorHistoryResponse.SensorHistoryResponseBuilder> grouped = new LinkedHashMap<>();

        for (var r : readings) {
            if (r.getReadingTime().isBefore(cutoffTime)) {
                continue;
            }

            String timestamp = r.getReadingTime().toString();
            grouped.putIfAbsent(timestamp, SensorHistoryResponse.builder().timestamp(timestamp));

            var builder = grouped.get(timestamp);
            String type = r.getSensor().getSensorType().getName().toLowerCase();

            if ("temperature".equals(type))    builder.temperature(r.getValue());
            else if ("vibration".equals(type)) builder.vibration(r.getValue());
            else if ("pressure".equals(type))  builder.pressure(r.getValue());
        }

        return grouped.values()
                .stream()
                .map(SensorHistoryResponse.SensorHistoryResponseBuilder::build)
                .toList();
    }
}