package com.minimaxi.backend.controller;

import com.minimaxi.backend.entity.Machine;
import com.minimaxi.backend.repository.MachineRepository;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/gateway")
@CrossOrigin(origins = "*")
public class GatewayController {

    private final MachineRepository machineRepository;

    public GatewayController(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @GetMapping("/assets/{assetId}/readings")
    public Map<String, Object> getReadings(@PathVariable String assetId) {

        Machine machine = machineRepository.findByAssetId(assetId)
                .orElse(null);

        // seed ثابت لكل ماشين + بيتغير كل 30 ثانية
        int seedBase = assetId.hashCode();
        long timeSlot = System.currentTimeMillis() / 30000;
        Random random = new Random(seedBase + timeSlot);

        String type = machine != null && machine.getMachineType() != null
                ? machine.getMachineType().toLowerCase()
                : "general";

        Map<String, Object> readings = generateReadings(type, random);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("assetId", assetId);
        response.put("machineType", type);
        response.put("timestamp", Instant.now().toString());
        response.put("readings", readings);
        response.put("gatewayStatus", "ONLINE");

        return response;
    }

    private Map<String, Object> generateReadings(String type, Random r) {
        Map<String, Object> readings = new LinkedHashMap<>();

        switch (type) {
            case "cnc machine", "cnc" -> {
                readings.put("Temperature",       round(65 + r.nextDouble() * 25));   // 65-90°C
                readings.put("Vibration",         round(0.3 + r.nextDouble() * 1.2)); // 0.3-1.5 mm/s
                readings.put("Pressure",          round(90 + r.nextDouble() * 30));   // 90-120 bar
                readings.put("Rotational Speed",  round(1200 + r.nextDouble() * 600));// 1200-1800 RPM
                readings.put("Thermal Efficiency",round(78 + r.nextDouble() * 15));   // 78-93%
            }
            case "pump", "hydraulic pump" -> {
                readings.put("Temperature",       round(40 + r.nextDouble() * 20));   // 40-60°C
                readings.put("Pressure",          round(150 + r.nextDouble() * 100)); // 150-250 bar
                readings.put("Vibration",         round(0.1 + r.nextDouble() * 0.8)); // 0.1-0.9 mm/s
                readings.put("Flow Rate",         round(80 + r.nextDouble() * 40));   // 80-120 L/min
                readings.put("Efficiency",        round(82 + r.nextDouble() * 12));   // 82-94%
            }
            case "engine", "industrial engine" -> {
                readings.put("Temperature",       round(75 + r.nextDouble() * 30));   // 75-105°C
                readings.put("Vibration",         round(0.5 + r.nextDouble() * 1.5)); // 0.5-2.0 mm/s
                readings.put("Pressure",          round(60 + r.nextDouble() * 40));   // 60-100 bar
                readings.put("Rotational Speed",  round(800 + r.nextDouble() * 400)); // 800-1200 RPM
                readings.put("Fuel Consumption",  round(15 + r.nextDouble() * 10));   // 15-25 L/h
            }
            case "compressor", "air compressor" -> {
                readings.put("Temperature",       round(50 + r.nextDouble() * 30));   // 50-80°C
                readings.put("Pressure",          round(6 + r.nextDouble() * 4));     // 6-10 bar
                readings.put("Vibration",         round(0.2 + r.nextDouble() * 0.6)); // 0.2-0.8 mm/s
                readings.put("Air Flow",          round(200 + r.nextDouble() * 100)); // 200-300 m³/h
                readings.put("Power Consumption", round(15 + r.nextDouble() * 10));   // 15-25 kW
            }
            default -> {
                readings.put("Temperature",       round(60 + r.nextDouble() * 30));
                readings.put("Vibration",         round(0.2 + r.nextDouble() * 1.0));
                readings.put("Pressure",          round(80 + r.nextDouble() * 40));
                readings.put("Efficiency",        round(75 + r.nextDouble() * 20));
            }
        }

        return readings;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}