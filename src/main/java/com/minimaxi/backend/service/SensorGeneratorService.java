package com.minimaxi.backend.service;

import com.minimaxi.backend.entity.Machine;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
public class SensorGeneratorService {

    private static final double[] NORMAL_MIN = {
            518.67, 641.73, 1578.80, 1392.45, 14.62, 21.60, 552.19,
            2387.96, 9035.03, 1.30, 47.05, 520.45, 2387.96, 8117.69,
            8.37, 0.030, 390.28, 2388.00, 100.00, 38.56, 23.14
    };

    private static final double[] NORMAL_MAX = {
            518.67, 643.38, 1599.33, 1420.50, 14.62, 21.61, 555.00,
            2388.19, 9087.65, 1.30, 47.87, 522.76, 2388.19, 8164.33,
            8.49, 0.030, 395.37, 2388.00, 100.00, 39.15, 23.49
    };

    // نسبة الزيادة المطلوبة فوق NORMAL_MAX عشان الموديل يحس بيها
    // sf=0.0 → 0%    → LOW
    // sf=0.5 → 0.75% → MEDIUM  (sensor_9: +68 فوق 9087 = 9155)
    // sf=1.0 → 2.5%  → HIGH    (sensor_9: +227 فوق 9087 = 9314)
    private static final double MAX_STRESS_PERCENT = 0.025;

    public List<Double> generate(Machine machine) {
        String assetId = machine.getAssetId() != null
                ? machine.getAssetId() : "MCH-" + machine.getId();
        String criticality = machine.getCriticality() != null
                ? machine.getCriticality().name().toLowerCase() : "medium";

        double sf = computeStressFactor(machine, criticality);

        long timeSlot = System.currentTimeMillis() / 600000;
        Random r = new Random(assetId.hashCode() + timeSlot);

        return generate21(sf, r);
    }

    private double computeStressFactor(Machine machine, String criticality) {
        double sfAge   = sfByAge(machine.getInstallationDate());
        double sfHours = sfByHours(machine);
        double base    = Math.max(sfAge, sfHours);

        if ("high".equals(criticality))        base = Math.min(base + 0.10, 1.0);
        else if ("medium".equals(criticality)) base = Math.min(base + 0.04, 1.0);

        return base;
    }

    private double sfByAge(LocalDate installationDate) {
        if (installationDate == null) return 0.3;
        long months = ChronoUnit.MONTHS.between(installationDate, LocalDate.now());
        if (months < 6)    return 0.0;
        if (months < 12)   return 0.18;
        if (months < 24)   return 0.35;
        if (months < 48)   return 0.55;
        if (months < 84)   return 0.75;
        return                     0.92;
    }

    private double sfByHours(Machine machine) {
        double hours = 0;
        if (machine.getOperatingHours() != null)
            hours = machine.getOperatingHours().doubleValue();
        else if (machine.getOperatingCycles() != null)
            hours = machine.getOperatingCycles().doubleValue() * 0.5;
        else return 0.3;

        if (hours < 500)     return 0.0;
        if (hours < 2000)    return 0.18;
        if (hours < 5000)    return 0.35;
        if (hours < 10000)   return 0.55;
        if (hours < 20000)   return 0.75;
        return                       0.92;
    }

    private List<Double> generate21(double sf, Random r) {
        double[] out = new double[21];

        for (int i = 0; i < 21; i++) {
            double min = NORMAL_MIN[i];
            double max = NORMAL_MAX[i];

            if (max - min < 0.001) {
                // Fixed sensor — نضيف stress كنسبة من القيمة نفسها
                double stress = min * MAX_STRESS_PERCENT * sf * (0.8 + r.nextDouble() * 0.4);
                out[i] = round(min + stress);
                continue;
            }

            // Base: random داخل الـ normal range
            double baseValue = min + r.nextDouble() * (max - min);

            // Stress: نسبة من NORMAL_MAX نفسه (مش من الـ range)
            // عشان يكون كافي يعدي الـ threshold بتاع الموديل
            double stress = max * MAX_STRESS_PERCENT * sf * (0.8 + r.nextDouble() * 0.4);

            out[i] = round(baseValue + stress);
        }

        return List.of(
                out[0], out[1], out[2], out[3], out[4], out[5], out[6],
                out[7], out[8], out[9], out[10], out[11], out[12], out[13],
                out[14], out[15], out[16], out[17], out[18], out[19], out[20]
        );
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}