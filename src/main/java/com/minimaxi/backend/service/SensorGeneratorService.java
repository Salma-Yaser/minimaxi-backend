package com.minimaxi.backend.service;

import com.minimaxi.backend.entity.Machine;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
public class SensorGeneratorService {

    // Normal MAX values from training data (upper bound of healthy range)
    private static final double[] NORMAL_MAX = {
            518.67,   // s1
            643.38,   // s2
            1599.33,  // s3  ← key sensor (Engine Temperature)
            1420.50,  // s4  ← key sensor (Compressor Pressure)
            14.62,    // s5
            21.61,    // s6
            555.00,   // s7
            2388.19,  // s8
            9087.65,  // s9  ← key sensor (Vibration) - most sensitive!
            1.30,     // s10
            47.87,    // s11 ← key sensor (Fuel Flow)
            522.76,   // s12
            2388.19,  // s13
            8164.33,  // s14 ← key sensor (Turbine Speed)
            8.49,     // s15
            0.030,    // s16
            395.37,   // s17 ← key sensor (Oil Pressure)
            2388.00,  // s18
            100.00,   // s19
            39.15,    // s20
            23.49     // s21
    };

    // Normal MIN values from training data
    private static final double[] NORMAL_MIN = {
            518.67,   // s1
            641.73,   // s2
            1578.80,  // s3
            1392.45,  // s4
            14.62,    // s5
            21.60,    // s6
            552.19,   // s7
            2387.96,  // s8
            9035.03,  // s9
            1.30,     // s10
            47.05,    // s11
            520.45,   // s12
            2387.96,  // s13
            8117.69,  // s14
            8.37,     // s15
            0.030,    // s16
            390.28,   // s17
            2388.00,  // s18
            100.00,   // s19
            38.56,    // s20
            23.14     // s21
    };

    public List<Double> generate(Machine machine) {
        String assetId = machine.getAssetId() != null
                ? machine.getAssetId() : "MCH-" + machine.getId();
        String criticality = machine.getCriticality() != null
                ? machine.getCriticality().name().toLowerCase() : "medium";

        // sf in [0.0 .. 1.0]
        // 0.0 = داخل الـ normal range تماماً → LOW
        // 0.3 = ~1% فوق الـ normal max    → MEDIUM
        // 0.6+ = ~2%+ فوق الـ normal max  → HIGH
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
        if (installationDate == null) return 0.25;
        long months = ChronoUnit.MONTHS.between(installationDate, LocalDate.now());
        if (months < 6)    return 0.0;   // جديدة → LOW
        if (months < 12)   return 0.15;  // أقل من سنة → LOW/border
        if (months < 24)   return 0.28;  // سنة لسنتين → MEDIUM
        if (months < 48)   return 0.50;  // 2-4 سنين → MEDIUM/HIGH
        if (months < 84)   return 0.72;  // 4-7 سنين → HIGH
        return                     0.90;  // 7+ سنين → HIGH
    }

    private double sfByHours(Machine machine) {
        double hours = 0;
        if (machine.getOperatingHours() != null)
            hours = machine.getOperatingHours().doubleValue();
        else if (machine.getOperatingCycles() != null)
            hours = machine.getOperatingCycles().doubleValue() * 0.5;
        else return 0.25;

        if (hours < 500)     return 0.0;
        if (hours < 2000)    return 0.15;
        if (hours < 5000)    return 0.28;
        if (hours < 10000)   return 0.52;
        if (hours < 20000)   return 0.74;
        return                       0.90;
    }

    private List<Double> generate21(double sf, Random r) {
        double[] out = new double[21];

        for (int i = 0; i < 21; i++) {
            double min   = NORMAL_MIN[i];
            double max   = NORMAL_MAX[i];
            double range = max - min;

            if (range < 0.001) {
                // Fixed sensor (min == max) — لا noise ولا stress
                out[i] = round(min);
                continue;
            }

            // داخل الـ normal range: قيمة random بين min و max
            double baseValue = min + r.nextDouble() * range;

            // stress: نسبة من الـ normal range تتضاف فوق الـ max
            // sf=0.0 → 0% زيادة (LOW)
            // sf=0.3 → ~30% من الـ range فوق الـ max (≈1% من القيمة → MEDIUM)
            // sf=0.6 → ~60% من الـ range فوق الـ max (≈2% من القيمة → HIGH)
            double stress = sf * range * 0.3 * (0.8 + r.nextDouble() * 0.4);

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