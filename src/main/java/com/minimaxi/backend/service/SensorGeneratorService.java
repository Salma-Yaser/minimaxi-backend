package com.minimaxi.backend.service;

import com.minimaxi.backend.entity.Machine;
import org.springframework.stereotype.Service;

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

    // sf zones:
    // 0.00 - 0.29 → LOW    (داخل normal range)
    // 0.30 - 0.59 → MEDIUM (~1% فوق normal max)
    // 0.60 - 1.00 → HIGH   (~2%+ فوق normal max)
    private static final double[] SF_STRESS_PERCENT = {
            0.0,   // sf < 0.30 → 0% stress → LOW
            0.012, // sf 0.30-0.59 → 1.2% stress → MEDIUM
            0.025  // sf >= 0.60 → 2.5% stress → HIGH
    };

    public List<Double> generate(Machine machine) {
        String assetId = machine.getAssetId() != null
                ? machine.getAssetId() : "MCH-" + machine.getId();

        double sf = computeStressFromAssetId(assetId, machine);

        long timeSlot = System.currentTimeMillis() / 600000;
        Random r = new Random(assetId.hashCode() + timeSlot);

        return generate21(sf, r);
    }

    private double computeStressFromAssetId(String assetId, Machine machine) {
        int hash = Math.abs(assetId.hashCode());
        double baseSf = (hash % 100) / 100.0;

        String criticality = machine.getCriticality() != null
                ? machine.getCriticality().name().toLowerCase() : "medium";

        if ("high".equals(criticality))     baseSf = Math.min(baseSf + 0.10, 1.0);
        else if ("low".equals(criticality)) baseSf = Math.max(baseSf - 0.10, 0.0);

        return baseSf;
    }

    private List<Double> generate21(double sf, Random r) {
        double[] out = new double[21];

        // نحدد الـ stress percent بناءً على الـ sf zone
        double stressPct;
        if (sf < 0.30)      stressPct = SF_STRESS_PERCENT[0];
        else if (sf < 0.60) stressPct = SF_STRESS_PERCENT[1];
        else                stressPct = SF_STRESS_PERCENT[2];

        for (int i = 0; i < 21; i++) {
            double min = NORMAL_MIN[i];
            double max = NORMAL_MAX[i];

            // base داخل الـ normal range
            double baseValue = (max - min < 0.001)
                    ? min
                    : min + r.nextDouble() * (max - min);

            // stress = نسبة ثابتة من الـ max + شوية random
            double stress = stressPct * max * (0.8 + r.nextDouble() * 0.4);

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