package com.minimaxi.backend.controller;

import com.minimaxi.backend.entity.Machine;
import com.minimaxi.backend.repository.MachineRepository;
import com.minimaxi.backend.service.SensorGeneratorService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gateway")
@CrossOrigin(origins = "*")
public class GatewayController {

    private final MachineRepository machineRepository;
    private final SensorGeneratorService sensorGenerator;

    public GatewayController(MachineRepository machineRepository,
                             SensorGeneratorService sensorGenerator) {
        this.machineRepository = machineRepository;
        this.sensorGenerator = sensorGenerator;
    }

    @GetMapping("/assets/{assetId}/readings")
    public Map<String, Object> getReadings(@PathVariable String assetId) {

        Machine machine = machineRepository.findByAssetId(assetId)
                .orElseThrow(() -> new RuntimeException("Machine not found: " + assetId));

        List<Double> data = sensorGenerator.generate(machine);

        Map<String, Object> readings = new LinkedHashMap<>();
        for (int i = 0; i < data.size(); i++) {
            readings.put("sensor_" + (i + 1), data.get(i));
        }

        Map<String, String> labels = new LinkedHashMap<>();
        labels.put("sensor_1",  "Temperature");
        labels.put("sensor_2",  "Pressure");
        labels.put("sensor_3",  "Rotational Speed");
        labels.put("sensor_4",  "Thermal Efficiency");
        labels.put("sensor_5",  "Airflow Dynamics");
        labels.put("sensor_6",  "Pressure Stability");
        labels.put("sensor_7",  "Vibration");
        labels.put("sensor_8",  "Temperature Stage");
        labels.put("sensor_9",  "Efficiency Parameter");
        labels.put("sensor_10", "Flow Variation");
        labels.put("sensor_11", "Vibration Amplitude");
        labels.put("sensor_12", "Pressure Ratio");
        labels.put("sensor_13", "Thermal Load");
        labels.put("sensor_14", "Mechanical Stress");
        labels.put("sensor_15", "Turbine Behavior");
        labels.put("sensor_16", "Air Intake Signal");
        labels.put("sensor_17", "Pressure Fluctuation");
        labels.put("sensor_18", "Heat Dissipation");
        labels.put("sensor_19", "Mechanical Oscillation");
        labels.put("sensor_20", "System Efficiency");
        labels.put("sensor_21", "Dynamic Vibration");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("assetId", assetId);
        response.put("machineType", machine.getMachineType());
        response.put("gatewayStatus", "ONLINE");
        response.put("timestamp", Instant.now().toString());
        response.put("readings", readings);
        response.put("labels", labels);
        response.put("aiPayload", Map.of("data", data));

        return response;
    }
}