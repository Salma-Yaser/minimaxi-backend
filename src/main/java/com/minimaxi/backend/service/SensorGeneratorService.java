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

    // LOW=0%, MEDIUM=4%, HIGH=12%
    private static final double[] SF_STRESS_PERCENT = {
            0.0,
            0.04,
            0.12
    };

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
                : "medium";

        switch (criticality) {
            case "low":
                // SF always 0.05 → 0.14  (below threshold 0.20) → Healthy
                return 0.05 + ((hash % 10) / 100.0);

            case "high":
                // SF always 0.55 → 0.64  (above threshold 0.45) → Critical
                return 0.55 + ((hash % 10) / 100.0);

            default: // medium
                // SF always 0.25 → 0.39  (between 0.20 and 0.45) → Warning
                return 0.25 + ((hash % 15) / 100.0);
        }
    }

    private List<Double> generate21(double sf, Random r) {

        double[] out = new double[21];

        double stressPct;
        if (sf < 0.20) {
            stressPct = SF_STRESS_PERCENT[0]; // 0%
        } else if (sf < 0.45) {
            stressPct = SF_STRESS_PERCENT[1]; // 4%
        } else {
            stressPct = SF_STRESS_PERCENT[2]; // 12%
        }

        for (int i = 0; i < 21; i++) {

            double min = NORMAL_MIN[i];
            double max = NORMAL_MAX[i];

            double baseValue = (max - min < 0.001)
                    ? min
                    : min + r.nextDouble() * (max - min);

            double stress = 0;

            switch (i) {
                case 3:  // sensor_4
                case 6:  // sensor_7
                case 8:  // sensor_9
                case 10: // sensor_11
                case 11: // sensor_12
                case 14: // sensor_15
                    double range = (max - min < 0.001) ? min * 0.01 : max - min;
                    stress = stressPct * range * (0.8 + r.nextDouble() * 0.4);
                    break;

                default:
                    double rangeD = (max - min < 0.001) ? min * 0.01 : max - min;
                    stress = stressPct * rangeD * 0.10 * (0.8 + r.nextDouble() * 0.4);
            }

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