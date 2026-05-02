package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.dto.request.*;
import com.minimaxi.backend.dto.response.*;
import com.minimaxi.backend.entity.AssetType;
import com.minimaxi.backend.entity.Threshold;
import com.minimaxi.backend.repository.*;
import com.minimaxi.backend.service.SettingsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class SettingsServiceImpl implements SettingsService {

    private final AssetTypeRepository assetTypeRepository;
    private final ThresholdRepository thresholdRepository;
    private final AiModelInfoRepository aiModelInfoRepository;
    private final OrganizationRepository organizationRepository;
    private final SensorRepository sensorRepository;
    private final AppUserRepository appUserRepository;

    public SettingsServiceImpl(AssetTypeRepository assetTypeRepository,
                               ThresholdRepository thresholdRepository,
                               AiModelInfoRepository aiModelInfoRepository,
                               OrganizationRepository organizationRepository,
                               SensorRepository sensorRepository,
                               AppUserRepository appUserRepository) {
        this.assetTypeRepository = assetTypeRepository;
        this.thresholdRepository = thresholdRepository;
        this.aiModelInfoRepository = aiModelInfoRepository;
        this.organizationRepository = organizationRepository;
        this.sensorRepository = sensorRepository;
        this.appUserRepository = appUserRepository;
    }

    // =========================================================================
    // ASSET TYPES
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<AssetTypeResponse> getAssetTypes() {
        return assetTypeRepository.findAll()
                .stream()
                .map(this::toAssetTypeResponse)
                .toList();
    }

    @Override
    @Transactional
    public AssetTypeResponse createAssetType(CreateAssetTypeRequest request) {
        AssetType assetType = new AssetType();
        assetType.setName(request.getName());
        assetType.setDescription(request.getDescription());
        assetType.setIndustry(request.getIndustry());
        assetType.setCreatedAt(Instant.now());
        assetType.setOrganization(
                organizationRepository.findById(request.getOrganizationId())
                        .orElseThrow(() -> new RuntimeException("Organization not found"))
        );
        return toAssetTypeResponse(assetTypeRepository.save(assetType));
    }

    @Override
    @Transactional
    public AssetTypeResponse updateAssetType(Long id, UpdateAssetTypeRequest request) {
        AssetType assetType = assetTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset type not found"));
        if (request.getName() != null)        assetType.setName(request.getName());
        if (request.getDescription() != null) assetType.setDescription(request.getDescription());
        if (request.getIndustry() != null)    assetType.setIndustry(request.getIndustry());
        return toAssetTypeResponse(assetTypeRepository.save(assetType));
    }

    @Override
    @Transactional
    public Map<String, Object> deleteAssetType(Long id) {
        if (!assetTypeRepository.existsById(id)) {
            throw new RuntimeException("Asset type not found");
        }
        assetTypeRepository.deleteById(id);
        return Map.of("success", true);
    }

    private AssetTypeResponse toAssetTypeResponse(AssetType a) {
        return new AssetTypeResponse(
                a.getId(),
                a.getName(),
                a.getDescription(),
                a.getIndustry(),
                a.getCreatedAt() != null ? a.getCreatedAt().toString() : null
        );
    }

    // =========================================================================
    // SENSOR THRESHOLDS
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<SensorThresholdResponse> getSensorThresholds() {
        return thresholdRepository.findAll()
                .stream()
                .map(this::toThresholdResponse)
                .toList();
    }

    @Override
    @Transactional
    public SensorThresholdResponse createSensorThreshold(CreateSensorThresholdRequest request) {
        Threshold threshold = new Threshold();

        threshold.setOrganization(
                organizationRepository.findById(request.getOrganizationId())
                        .orElseThrow(() -> new RuntimeException("Organization not found"))
        );
        threshold.setAssetType(
                assetTypeRepository.findById(request.getAssetTypeId())
                        .orElseThrow(() -> new RuntimeException("Asset type not found"))
        );

        var sensor = sensorRepository.findById(request.getSensorTypeId())
                .orElseThrow(() -> new RuntimeException("Sensor not found"));
        threshold.setSensorType(sensor.getSensorType());

        threshold.setWarningValue(BigDecimal.valueOf(request.getWarningValue()));
        threshold.setCriticalValue(BigDecimal.valueOf(request.getCriticalValue()));
        threshold.setUpdatedAt(Instant.now());
        threshold.setUpdatedByUser(
                appUserRepository.findById(request.getUpdatedByUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"))
        );

        return toThresholdResponse(thresholdRepository.save(threshold));
    }

    @Override
    @Transactional
    public SensorThresholdResponse updateSensorThreshold(Long id, UpdateSensorThresholdRequest request) {
        Threshold threshold = thresholdRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Threshold not found"));

        threshold.setWarningValue(BigDecimal.valueOf(request.getWarningValue()));
        threshold.setCriticalValue(BigDecimal.valueOf(request.getCriticalValue()));
        threshold.setUpdatedAt(Instant.now());
        threshold.setUpdatedByUser(
                appUserRepository.findById(request.getUpdatedByUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"))
        );

        return toThresholdResponse(thresholdRepository.save(threshold));
    }

    @Override
    @Transactional
    public Map<String, Object> deleteSensorThreshold(Long id) {
        if (!thresholdRepository.existsById(id)) {
            throw new RuntimeException("Threshold not found");
        }
        thresholdRepository.deleteById(id);
        return Map.of("success", true);
    }

    private SensorThresholdResponse toThresholdResponse(Threshold t) {
        return new SensorThresholdResponse(
                t.getId(),
                t.getAssetType().getId(),
                t.getAssetType().getName(),
                t.getSensorType().getId(),
                t.getSensorType().getName(),
                t.getSensorType().getUnit(),
                t.getWarningValue().doubleValue(),
                t.getCriticalValue().doubleValue(),
                t.getUpdatedAt() != null ? t.getUpdatedAt().toString() : null
        );
    }

    // =========================================================================
    // AI MODEL
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public AIModelInfoResponse getAIModelInfo() {
        return aiModelInfoRepository.findAll()
                .stream()
                .findFirst()
                .map(m -> new AIModelInfoResponse(
                        m.getId(),
                        m.getModelName(),
                        m.getVersion(),
                        m.getLastTrainingDate() != null ? m.getLastTrainingDate().toString() : null,
                        m.getNotes(),
                        m.getFeaturesUsedJson()
                ))
                .orElse(new AIModelInfoResponse(
                        null,
                        "MiniMaxi Predictive Model",
                        "v1.0",
                        null,
                        "No model trained yet",
                        null
                ));
    }

    @Override
    public Map<String, Object> retrainAIModel() {
        return Map.of(
                "success", true,
                "message", "Retraining scheduled successfully",
                "status", "pending"
        );
    }

    @Override
    public Map<String, Object> scheduleTraining(Map<String, Object> data) {
        return Map.of(
                "success", true,
                "message", "Training scheduled",
                "scheduledAt", data.getOrDefault("scheduledAt", "N/A")
        );
    }
}