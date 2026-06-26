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

    // Actual max values from NASA training data during real failures
    // Never generate values above these — model has never seen higher
    private static final double[] FAILURE_MAX = {
            518.67, 644.53, 1616.91, 1441.49, 14.62, 21.61, 556.06,
            2388.56, 9244.59, 1.30, 48.53, 523.38, 2388.56, 8293.72,
            8.58, 0.03, 400.00, 2388.00, 100.00, 39.43, 23.62
    };

    private static final int[] KEY_SENSORS = {3, 6, 8, 10, 11, 14};

    public List<Double> generate(Machine machine) {
        String assetId = machine.getAssetId() != null
                ? machine.getAssetId()
                : "MCH-" + machine.getId();

        long timeSlot = System.currentTimeMillis() / 600000;
        Random r = new Random(assetId.hashCode() + timeSlot);

        double sf = computeStressFromAssetId(assetId, machine);

        System.out.println(
                "Machine=" + assetId +
                        " Criticality=" + machine.getCriticality() +
                        " SF=" + sf
        );

        return generate21(sf, r);
    }

    private double computeStressFromAssetId(String assetId, Machine machine) {
        int hash = Math.abs(assetId.hashCode());

        String criticality = machine.getCriticality() != null
                ? machine.getCriticality().name().toLowerCase()
                : "low";

        switch (criticality) {
            case "low":
                // SF 0.05 → 0.14 — always below 0.20 → Healthy
                return 0.05 + ((hash % 10) / 100.0);
            case "high":
                // SF 0.55 → 0.64 — always above 0.45 → Critical
                return 0.55 + ((hash % 10) / 100.0);
            default: // medium
                // SF 0.25 → 0.39 — always between 0.20 and 0.45 → Warning
                return 0.25 + ((hash % 15) / 100.0);
        }
    }

    private List<Double> generate21(double sf, Random r) {
        double[] out = new double[21];

        double pushFactor;
        if (sf < 0.20) {
            pushFactor = 0.0;   // Healthy — stay inside normal range
        } else if (sf < 0.45) {
            pushFactor = 0.35;  // Warning — 35% toward failure ceiling
        } else {
            pushFactor = 0.80;  // Critical — 80% toward failure ceiling
        }

        for (int i = 0; i < 21; i++) {
            double min = NORMAL_MIN[i];
            double max = NORMAL_MAX[i];
            double failMax = FAILURE_MAX[i];

            double baseValue = (max - min < 0.001)
                    ? min
                    : min + r.nextDouble() * (max - min);

            double stress = 0;

            if (pushFactor > 0) {
                double headroom = failMax - max;
                if (isKeySensor(i)) {
                    // Key sensors — full push
                    stress = headroom * pushFactor * (0.8 + r.nextDouble() * 0.4);
                } else {
                    // Non-key sensors — small nudge
                    stress = headroom * pushFactor * 0.25 * (0.8 + r.nextDouble() * 0.4);
                }
            }

            out[i] = round(baseValue + stress);
        }

        return List.of(
                out[0], out[1], out[2], out[3], out[4], out[5], out[6],
                out[7], out[8], out[9], out[10], out[11], out[12], out[13],
                out[14], out[15], out[16], out[17], out[18], out[19], out[20]
        );
    }

    private boolean isKeySensor(int index) {
        for (int k : KEY_SENSORS) {
            if (index == k) return true;
        }
        return false;
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}