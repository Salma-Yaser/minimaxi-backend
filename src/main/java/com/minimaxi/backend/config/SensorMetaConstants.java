package com.minimaxi.backend.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Single source of truth for the 21 simulated sensors.
 * Index in the arrays/lists below is 0-based and corresponds to
 * sensor_1 .. sensor_21 (externalRef = "sensor_" + (index + 1)).
 *
 * Keeping this in one place means SensorGeneratorService output order,
 * the Sensor/SensorType seeding, and the Gateway responses can never
 * drift apart.
 */
public final class SensorMetaConstants {

    private SensorMetaConstants() {
    }

    public static final int SENSOR_COUNT = 21;

    public static final String[] NAMES = {
            "Temperature",              // sensor_1
            "Pressure",                 // sensor_2
            "Rotational Speed",         // sensor_3
            "Thermal Efficiency",       // sensor_4
            "Airflow Dynamics",         // sensor_5
            "Pressure Stability",       // sensor_6
            "Vibration",                // sensor_7
            "Temperature Stage",        // sensor_8
            "Efficiency Parameter",     // sensor_9
            "Flow Variation",           // sensor_10
            "Vibration Amplitude",      // sensor_11
            "Pressure Ratio",           // sensor_12
            "Thermal Load",             // sensor_13
            "Mechanical Stress",        // sensor_14
            "Turbine Behavior",         // sensor_15
            "Air Intake Signal",        // sensor_16
            "Pressure Fluctuation",     // sensor_17
            "Heat Dissipation",         // sensor_18
            "Mechanical Oscillation",   // sensor_19
            "System Efficiency",        // sensor_20
            "Dynamic Vibration"         // sensor_21
    };

    // Units kept simple/consistent; adjust if you have stricter per-sensor units.
    public static final String[] UNITS = {
            "°C", "bar", "rpm", "%", "m3/s", "%", "mm/s", "°C", "%", "%",
            "mm/s", "ratio", "%", "MPa", "%", "signal", "%", "%", "Hz", "%", "mm/s"
    };

    public static String externalRef(int zeroBasedIndex) {
        return "sensor_" + (zeroBasedIndex + 1);
    }

    public static Map<String, String> labelsBySensorKey() {
        Map<String, String> labels = new LinkedHashMap<>();
        for (int i = 0; i < SENSOR_COUNT; i++) {
            labels.put(externalRef(i), NAMES[i]);
        }
        return labels;
    }

    /**
     * Zero-based indices (into NAMES/UNITS, and into the List<Double>
     * returned by SensorGeneratorService.generate()) for the 3 sensors
     * the frontend currently tracks and charts:
     *   sensor_1 -> Temperature
     *   sensor_2 -> Pressure
     *   sensor_7 -> Vibration
     *
     * Only these are persisted by SensorReadingScheduler today. Add more
     * indices here later if the frontend starts charting other sensors.
     */
    public static final int[] TRACKED_SENSOR_INDICES = { 0, 1, 6 };
}