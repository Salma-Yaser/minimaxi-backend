package com.minimaxi.backend.service;

import com.minimaxi.backend.entity.Machine;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class SensorGeneratorService {

    public List<Double> generate(Machine machine) {
        String assetId = machine.getAssetId() != null
                ? machine.getAssetId() : "MCH-" + machine.getId();
        String type = machine.getMachineType() != null
                ? machine.getMachineType().toLowerCase() : "general";
        String status = machine.getStatus() != null
                ? machine.getStatus().name().toLowerCase() : "healthy";
        String criticality = machine.getCriticality() != null
                ? machine.getCriticality().name().toLowerCase() : "medium";

        double sf = 1.0;
        if ("critical".equals(status))     sf = 1.8;
        else if ("warning".equals(status)) sf = 1.35;
        else                                sf = 0.85; // healthy

        if ("high".equals(criticality))    sf += 0.25;
        else if ("medium".equals(criticality)) sf += 0.1;

        long timeSlot = System.currentTimeMillis() / 30000;
        Random r = new Random(assetId.hashCode() + timeSlot);

        return generate21(type, sf, r);
    }

    private List<Double> generate21(String type, double sf, Random r) {
        return switch (type) {
            case "cnc machine", "cnc" -> List.of(
                    round(580 + r.nextDouble() * 80 * sf),
                    round(680 + r.nextDouble() * 60 * sf),
                    round(1800 + r.nextDouble() * 200 * sf),
                    round(1500 + r.nextDouble() * 200 * sf),
                    round(28 + r.nextDouble() * 10 * sf),
                    round(42 + r.nextDouble() * 12 * sf),
                    round(680 + r.nextDouble() * 60 * sf),
                    round(2400 + r.nextDouble() * 200 * sf),
                    round(9800 + r.nextDouble() * 800 * sf),
                    round(1.8 + r.nextDouble() * 0.4 * sf),
                    round(85 + r.nextDouble() * 10 * sf),
                    round(600 + r.nextDouble() * 80 * sf),
                    round(2400 + r.nextDouble() * 200 * sf),
                    round(8800 + r.nextDouble() * 800 * sf),
                    round(10.0 + r.nextDouble() * 4 * sf),
                    round(0.10 + r.nextDouble() * 0.05 * sf),
                    round(490 + r.nextDouble() * 40 * sf),
                    round(2450 + r.nextDouble() * 150 * sf),
                    round(82 + r.nextDouble() * 8 * sf),
                    round(48 + r.nextDouble() * 10 * sf),
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
                    round(9.0 + r.nextDouble() * 5 * sf),
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
                    round(13.0 + r.nextDouble() * 5 * sf),
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
                    round(11.0 + r.nextDouble() * 4 * sf),
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
                    round(11.0 + r.nextDouble() * 4 * sf),
                    round(0.11 + r.nextDouble() * 0.05 * sf),
                    round(500 + r.nextDouble() * 40 * sf),
                    round(2450 + r.nextDouble() * 150 * sf),
                    round(83 + r.nextDouble() * 8 * sf),
                    round(50 + r.nextDouble() * 10 * sf),
                    round(39 + r.nextDouble() * 8 * sf)
            );
        };
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}