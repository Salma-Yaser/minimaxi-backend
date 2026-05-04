package com.minimaxi.backend.controller;

import com.minimaxi.backend.dto.request.*;
import com.minimaxi.backend.dto.response.*;
import com.minimaxi.backend.service.SettingsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "http://localhost:5173")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    // ─── Asset Types ─────────────────────────────────────────────────────────

    // Frontend calls: GET /api/settings/asset-types  ✅
    @GetMapping("/asset-types")
    public List<AssetTypeResponse> getAssetTypes() {
        return settingsService.getAssetTypes();
    }

    // Frontend calls: POST /api/settings/asset-types  ✅
    @PostMapping("/asset-types")
    public AssetTypeResponse createAssetType(@RequestBody CreateAssetTypeRequest request) {
        return settingsService.createAssetType(request);
    }

    // Frontend calls: PUT /api/settings/asset-types/{id}  ✅
    @PutMapping("/asset-types/{id}")
    public AssetTypeResponse updateAssetType(
            @PathVariable Long id,
            @RequestBody UpdateAssetTypeRequest request
    ) {
        return settingsService.updateAssetType(id, request);
    }

    // Frontend calls: DELETE /api/settings/asset-types/{id}  ✅
    @DeleteMapping("/asset-types/{id}")
    public Map<String, Object> deleteAssetType(@PathVariable Long id) {
        return settingsService.deleteAssetType(id);
    }

    // ─── Sensor Thresholds ───────────────────────────────────────────────────

    // Frontend calls: GET /api/settings/sensor-thresholds  ✅
    @GetMapping("/sensor-thresholds")
    public List<SensorThresholdResponse> getSensorThresholds() {
        return settingsService.getSensorThresholds();
    }


    // Frontend calls: GET /api/settings/sensor-types  ✅
    @GetMapping("/sensor-types")
    public List<SensorTypeResponse> getSensorTypes() {
        return settingsService.getSensorTypes();
    }

    // Frontend calls: POST /api/settings/sensor-thresholds  ✅
    @PostMapping("/sensor-thresholds")
    public SensorThresholdResponse createSensorThreshold(
            @RequestBody CreateSensorThresholdRequest request
    ) {
        return settingsService.createSensorThreshold(request);
    }

    // Frontend calls: PUT /api/settings/sensor-thresholds/{id}  ✅
    @PutMapping("/sensor-thresholds/{id}")
    public SensorThresholdResponse updateSensorThreshold(
            @PathVariable Long id,
            @RequestBody UpdateSensorThresholdRequest request
    ) {
        return settingsService.updateSensorThreshold(id, request);
    }

    // Frontend calls: DELETE /api/settings/sensor-thresholds/{id}  ✅
    @DeleteMapping("/sensor-thresholds/{id}")
    public Map<String, Object> deleteSensorThreshold(@PathVariable Long id) {
        return settingsService.deleteSensorThreshold(id);
    }

    // ─── AI Model ────────────────────────────────────────────────────────────

    // Frontend calls: GET /api/settings/ai-model  ✅
    @GetMapping("/ai-model")
    public AIModelInfoResponse getAIModelInfo() {
        return settingsService.getAIModelInfo();
    }

    // Frontend calls: POST /api/settings/ai-model/retrain  ✅
    @PostMapping("/ai-model/retrain")
    public Map<String, Object> retrainAIModel() {
        return settingsService.retrainAIModel();
    }

    // Frontend calls: POST /api/settings/ai-model/schedule  ✅
    @PostMapping("/ai-model/schedule")
    public Map<String, Object> scheduleTraining(@RequestBody Map<String, Object> data) {
        return settingsService.scheduleTraining(data);
    }
}