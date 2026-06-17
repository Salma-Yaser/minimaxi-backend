package com.minimaxi.backend.service;

import com.minimaxi.backend.entity.Machine;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

/**
 * Generates 21 sensor readings aligned with the AI model's training data ranges.
 *
 * Strategy:
 *   - Each sensor has a "normal baseline" from the model's training data.
 *   - stress factor (sf) shifts values ABOVE normal to simulate degradation.
 *   - sf = 0.0 → perfectly healthy (values at baseline)
 *   - sf = 1.0 → clearly degraded  (values ~20-30% above baseline)
 *   - sf is derived from machine age + operating hours, NOT from status
 *     (avoids feedback loop where CRITICAL status → high sf → CRITICAL forever)
 */
@Service
public class SensorGeneratorService {

    // ── Baseline = midpoint of training "normal" range ──────────────────────
    // sensor_1..21 for CNC (only type we have exact ranges for)
    private static final double[] CNC_BASE = {
            518.67,   // s1  Temperature
            642.56,   // s2  Pressure
            1589.07,  // s3  Rotational Speed
            1406.48,  // s4  Thermal Efficiency
            14.62,    // s5  Airflow Dynamics          (fixed in training)
            21.61,    // s6  Pressure Stability         (very tight range)
            553.60,   // s7  Vibration
            2388.08,  // s8  Temperature Stage          (very tight)
            9061.35,  // s9  Efficiency Parameter
            1.30,     // s10 Flow Variation             (fixed)
            47.47,    // s11 Vibration Amplitude
            521.61,   // s12 Pressure Ratio
            2388.08,  // s13 Thermal Load               (very tight)
            8141.02,  // s14 Mechanical Stress
            8.43,     // s15 Turbine Behavior
            0.030,    // s16 Air Intake Signal          (fixed)
            392.83,   // s17 Pressure Fluctuation
            2388.00,  // s18 Heat Dissipation           (fixed)
            100.00,   // s19 Mechanical Oscillation     (fixed)
            38.86,    // s20 System Efficiency
            23.32     // s21 Dynamic Vibration
    };

    // Max deviation above baseline at sf=1.0 (tuned so model sees HIGH/CRITICAL)
    // Sensors with very tight ranges get smaller deviation to stay realistic
    private static final double[] CNC_DELTA = {
            30.0,    // s1
            8.0,     // s2
            80.0,    // s3
            80.0,    // s4
            3.0,     // s5
            0.5,     // s6  tight!
            10.0,    // s7
            1.0,     // s8  tight!
            500.0,   // s9  model is very sensitive here
            0.15,    // s10
            8.0,     // s11
            12.0,    // s12
            1.0,     // s13 tight!
            300.0,   // s14
            2.0,     // s15
            0.005,   // s16 tight!
            25.0,    // s17
            5.0,     // s18
            2.0,     // s19
            3.0,     // s20
            2.0      // s21
    };

    public List<Double> generate(Machine machine) {
        String assetId = machine.getAssetId() != null
                ? machine.getAssetId() : "MCH-" + machine.getId();
        String type = machine.getMachineType() != null
                ? machine.getMachineType().toLowerCase() : "general";
        String criticality = machine.getCriticality() != null
                ? machine.getCriticality().name().toLowerCase() : "medium";

        // sf in [0.0 .. 1.0] — 0=healthy, 1=critical
        double sf = computeStressFactor(machine, criticality);

        // Seed changes every 10 min (aligned with scheduler)
        long timeSlot = System.currentTimeMillis() / 600000;
        Random r = new Random(assetId.hashCode() + timeSlot);

        return generate21(type, sf, r);
    }

    /**
     * sf based on age + operating hours — never on machine.status
     * to avoid the feedback loop.
     */
    private double computeStressFactor(Machine machine, String criticality) {
        double sfAge   = sfByAge(machine.getInstallationDate());
        double sfHours = sfByHours(machine);
        double base    = Math.max(sfAge, sfHours);   // worst of the two wins

        if ("high".equals(criticality))        base = Math.min(base + 0.15, 1.0);
        else if ("medium".equals(criticality)) base = Math.min(base + 0.05, 1.0);

        return base;
    }

    private double sfByAge(LocalDate installationDate) {
        if (installationDate == null) return 0.3;
        long months = ChronoUnit.MONTHS.between(installationDate, LocalDate.now());
        if (months < 6)    return 0.05;
        if (months < 12)   return 0.15;
        if (months < 24)   return 0.30;
        if (months < 48)   return 0.50;
        if (months < 84)   return 0.72;
        return                     0.90;
    }

    private double sfByHours(Machine machine) {
        double hours = 0;
        if (machine.getOperatingHours() != null)
            hours = machine.getOperatingHours().doubleValue();
        else if (machine.getOperatingCycles() != null)
            hours = machine.getOperatingCycles().doubleValue() * 0.5;
        else return 0.3;

        if (hours < 500)     return 0.05;
        if (hours < 2000)    return 0.15;
        if (hours < 5000)    return 0.30;
        if (hours < 10000)   return 0.55;
        if (hours < 20000)   return 0.75;
        return                       0.92;
    }

    private List<Double> generate21(String type, double sf, Random r) {
        double[] base, delta;

        // كل الـ types بتاخد نفس الـ CNC baseline لأن الـ model اتدرب عليه
        // الاختلاف بس في شوية random noise عشان يبان فيه تنوع
        base  = CNC_BASE;
        delta = CNC_DELTA;

        double[] out = new double[21];
        for (int i = 0; i < 21; i++) {
            // noise صغير جداً حتى لو sf = 0 (عشان مش تبقى أرقام ثابتة تماماً)
            double noise = (r.nextDouble() - 0.5) * delta[i] * 0.1;
            // الزيادة الرئيسية بناءً على sf
            double stress = r.nextDouble() * delta[i] * sf;
            out[i] = round(base[i] + noise + stress);
        }

        return List.of(
                out[0],out[1],out[2],out[3],out[4],out[5],out[6],
                out[7],out[8],out[9],out[10],out[11],out[12],out[13],
                out[14],out[15],out[16],out[17],out[18],out[19],out[20]
        );
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}