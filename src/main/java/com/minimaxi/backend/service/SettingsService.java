package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.request.*;
import com.minimaxi.backend.dto.response.*;

import java.util.List;
import java.util.Map;

public interface SettingsService {

    // ─── Asset Types ─────────────────────────────────────────────────────────
    List<AssetTypeResponse> getAssetTypes();
    AssetTypeResponse createAssetType(CreateAssetTypeRequest request);
    AssetTypeResponse updateAssetType(Long id, UpdateAssetTypeRequest request);
    Map<String, Object> deleteAssetType(Long id);

    // ─── Sensor Thresholds ───────────────────────────────────────────────────
    List<SensorThresholdResponse> getSensorThresholds();
    SensorThresholdResponse createSensorThreshold(CreateSensorThresholdRequest request);
    SensorThresholdResponse updateSensorThreshold(Long id, UpdateSensorThresholdRequest request);
    Map<String, Object> deleteSensorThreshold(Long id);

    List<SensorTypeResponse> getSensorTypes();


    // ─── AI Model ────────────────────────────────────────────────────────────
    AIModelInfoResponse getAIModelInfo();
    Map<String, Object> retrainAIModel();
    Map<String, Object> scheduleTraining(Map<String, Object> data);
}