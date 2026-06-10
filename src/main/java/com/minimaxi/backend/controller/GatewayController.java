package com.minimaxi.backend.controller;

import com.minimaxi.backend.entity.Machine;
import com.minimaxi.backend.repository.MachineRepository;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
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

        Machine machine = machineRepository.findByAssetId(assetId).orElse(null);

        String type = machine != null && machine.getMachineType() != null
                ? machine.getMachineType().toLowerCase()
                : "general";

        String status = machine != null && machine.getStatus() != null
                ? machine.getStatus().name().toLowerCase()
                : "healthy";

        String criticality = machine != null && machine.getCriticality() != null
                ? machine.getCriticality().name().toLowerCase()
                : "medium";

        // seed ثابت لكل ماشين + بيتغير كل 30 ثانية
        int seedBase = assetId.hashCode();
        long timeSlot = System.currentTimeMillis() / 30000;
        Random r = new Random(seedBase + timeSlot);

        List<Double> data = generate21Sensors(type, status, criticality, r);

        Map<String, Object> readings = new LinkedHashMap<>();
        readings.put("sensor_1",  data.get(0));
        readings.put("sensor_2",  data.get(1));
        readings.put("sensor_3",  data.get(2));
        readings.put("sensor_4",  data.get(3));
        readings.put("sensor_5",  data.get(4));
        readings.put("sensor_6",  data.get(5));
        readings.put("sensor_7",  data.get(6));
        readings.put("sensor_8",  data.get(7));
        readings.put("sensor_9",  data.get(8));
        readings.put("sensor_10", data.get(9));
        readings.put("sensor_11", data.get(10));
        readings.put("sensor_12", data.get(11));
        readings.put("sensor_13", data.get(12));
        readings.put("sensor_14", data.get(13));
        readings.put("sensor_15", data.get(14));
        readings.put("sensor_16", data.get(15));
        readings.put("sensor_17", data.get(16));
        readings.put("sensor_18", data.get(17));
        readings.put("sensor_19", data.get(18));
        readings.put("sensor_20", data.get(19));
        readings.put("sensor_21", data.get(20));

        // أسماء بشرية للعرض في الفرونت
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
        response.put("machineType", type);
        response.put("timestamp", Instant.now().toString());
        response.put("readings", readings);
        response.put("labels", labels);
        response.put("gatewayStatus", "ONLINE");

        // ✅ format جاهز للـ AI model مباشرة
        response.put("aiPayload", Map.of("data", data));

        return response;
    }

    private List<Double> generate21Sensors(String type, String status, String criticality, Random r) {

        // معامل الضغط بناءً على الـ status والـ criticality
        double stressFactor = 1.0;
        if ("critical".equals(status))   stressFactor = 1.4;
        else if ("warning".equals(status)) stressFactor = 1.2;
        if ("high".equals(criticality))  stressFactor += 0.1;

        double sf = stressFactor;

        return switch (type) {
            case "cnc machine", "cnc" -> List.of(
                    // sensor_1  Temperature
                    round(580 + r.nextDouble() * 80 * sf),
                    // sensor_2  Pressure
                    round(680 + r.nextDouble() * 60 * sf),
                    // sensor_3  Rotational Speed
                    round(1800 + r.nextDouble() * 200 * sf),
                    // sensor_4  Thermal Efficiency
                    round(1500 + r.nextDouble() * 200 * sf),
                    // sensor_5  Airflow Dynamics
                    round(28 + r.nextDouble() * 10 * sf),
                    // sensor_6  Pressure Stability
                    round(42 + r.nextDouble() * 12 * sf),
                    // sensor_7  Vibration
                    round(680 + r.nextDouble() * 60 * sf),
                    // sensor_8  Temperature Stage
                    round(2400 + r.nextDouble() * 200 * sf),
                    // sensor_9  Efficiency Parameter
                    round(9800 + r.nextDouble() * 800 * sf),
                    // sensor_10 Flow Variation
                    round(1.8 + r.nextDouble() * 0.4 * sf),
                    // sensor_11 Vibration Amplitude
                    round(85 + r.nextDouble() * 10 * sf),
                    // sensor_12 Pressure Ratio
                    round(600 + r.nextDouble() * 80 * sf),
                    // sensor_13 Thermal Load
                    round(2400 + r.nextDouble() * 200 * sf),
                    // sensor_14 Mechanical Stress
                    round(8800 + r.nextDouble() * 800 * sf),
                    // sensor_15 Turbine Behavior
                    round(10 + r.nextDouble() * 4 * sf),
                    // sensor_16 Air Intake Signal
                    round(0.10 + r.nextDouble() * 0.05 * sf),
                    // sensor_17 Pressure Fluctuation
                    round(490 + r.nextDouble() * 40 * sf),
                    // sensor_18 Heat Dissipation
                    round(2450 + r.nextDouble() * 150 * sf),
                    // sensor_19 Mechanical Oscillation
                    round(82 + r.nextDouble() * 8 * sf),
                    // sensor_20 System Efficiency
                    round(48 + r.nextDouble() * 10 * sf),
                    // sensor_21 Dynamic Vibration
                    round(38 + r.nextDouble() * 8 * sf)
            );

            case "pump", "hydraulic pump" -> List.of(
                    round(520 + r.nextDouble() * 60 * sf),
                    round(720 + r.nextDouble() * 80 * sf),
                    round(1600 + r.nextDouble() * 300 * sf),
                    round(1400 + r.nextDouble() * 250 * sf),
                    round(25 + r.nextDouble() * 12 * sf),
                    round(38 + r.nextDouble() * 15 * sf),
                    round(640 + r.nextDouble() * 80 * sf),
                    round(2200 + r.nextDouble() * 300 * sf),
                    round(9000 + r.nextDouble() * 1000 * sf),
                    round(1.6 + r.nextDouble() * 0.5 * sf),
                    round(80 + r.nextDouble() * 12 * sf),
                    round(580 + r.nextDouble() * 100 * sf),
                    round(2200 + r.nextDouble() * 300 * sf),
                    round(8200 + r.nextDouble() * 1000 * sf),
                    round(9 + r.nextDouble() * 5 * sf),
                    round(0.09 + r.nextDouble() * 0.06 * sf),
                    round(460 + r.nextDouble() * 60 * sf),
                    round(2300 + r.nextDouble() * 200 * sf),
                    round(78 + r.nextDouble() * 10 * sf),
                    round(45 + r.nextDouble() * 12 * sf),
                    round(35 + r.nextDouble() * 10 * sf)
            );

            case "engine", "industrial engine" -> List.of(
                    round(700 + r.nextDouble() * 100 * sf),
                    round(760 + r.nextDouble() * 80 * sf),
                    round(2000 + r.nextDouble() * 300 * sf),
                    round(1700 + r.nextDouble() * 200 * sf),
                    round(38 + r.nextDouble() * 15 * sf),
                    round(55 + r.nextDouble() * 15 * sf),
                    round(760 + r.nextDouble() * 80 * sf),
                    round(2600 + r.nextDouble() * 300 * sf),
                    round(11000 + r.nextDouble() * 1000 * sf),
                    round(2.2 + r.nextDouble() * 0.5 * sf),
                    round(92 + r.nextDouble() * 8 * sf),
                    round(700 + r.nextDouble() * 80 * sf),
                    round(2600 + r.nextDouble() * 300 * sf),
                    round(10000 + r.nextDouble() * 1200 * sf),
                    round(13 + r.nextDouble() * 5 * sf),
                    round(0.14 + r.nextDouble() * 0.05 * sf),
                    round(540 + r.nextDouble() * 60 * sf),
                    round(2700 + r.nextDouble() * 200 * sf),
                    round(90 + r.nextDouble() * 8 * sf),
                    round(58 + r.nextDouble() * 10 * sf),
                    round(46 + r.nextDouble() * 8 * sf)
            );

            case "compressor", "air compressor" -> List.of(
                    round(560 + r.nextDouble() * 70 * sf),
                    round(700 + r.nextDouble() * 70 * sf),
                    round(1700 + r.nextDouble() * 250 * sf),
                    round(1550 + r.nextDouble() * 200 * sf),
                    round(30 + r.nextDouble() * 12 * sf),
                    round(44 + r.nextDouble() * 12 * sf),
                    round(700 + r.nextDouble() * 70 * sf),
                    round(2300 + r.nextDouble() * 250 * sf),
                    round(9500 + r.nextDouble() * 900 * sf),
                    round(1.9 + r.nextDouble() * 0.4 * sf),
                    round(87 + r.nextDouble() * 10 * sf),
                    round(620 + r.nextDouble() * 90 * sf),
                    round(2300 + r.nextDouble() * 250 * sf),
                    round(8600 + r.nextDouble() * 900 * sf),
                    round(11 + r.nextDouble() * 4 * sf),
                    round(0.11 + r.nextDouble() * 0.05 * sf),
                    round(500 + r.nextDouble() * 50 * sf),
                    round(2400 + r.nextDouble() * 200 * sf),
                    round(84 + r.nextDouble() * 9 * sf),
                    round(50 + r.nextDouble() * 10 * sf),
                    round(40 + r.nextDouble() * 8 * sf)
            );

            default -> List.of(
                    round(600 + r.nextDouble() * 100 * sf),
                    round(700 + r.nextDouble() * 80 * sf),
                    round(1800 + r.nextDouble() * 300 * sf),
                    round(1550 + r.nextDouble() * 200 * sf),
                    round(30 + r.nextDouble() * 12 * sf),
                    round(45 + r.nextDouble() * 12 * sf),
                    round(700 + r.nextDouble() * 80 * sf),
                    round(2400 + r.nextDouble() * 200 * sf),
                    round(9500 + r.nextDouble() * 800 * sf),
                    round(1.9 + r.nextDouble() * 0.4 * sf),
                    round(87 + r.nextDouble() * 8 * sf),
                    round(630 + r.nextDouble() * 80 * sf),
                    round(2400 + r.nextDouble() * 200 * sf),
                    round(8800 + r.nextDouble() * 800 * sf),
                    round(11 + r.nextDouble() * 4 * sf),
                    round(0.11 + r.nextDouble() * 0.05 * sf),
                    round(500 + r.nextDouble() * 40 * sf),
                    round(2450 + r.nextDouble() * 150 * sf),
                    round(83 + r.nextDouble() * 8 * sf),
                    round(50 + r.nextDouble() * 10 * sf),
                    round(39 + r.nextDouble() * 8 * sf)
            );
        };
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}