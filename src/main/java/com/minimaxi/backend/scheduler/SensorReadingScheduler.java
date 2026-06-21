package com.minimaxi.backend.scheduler;

import com.minimaxi.backend.config.SensorMetaConstants;
import com.minimaxi.backend.entity.Machine;
import com.minimaxi.backend.entity.Sensor;
import com.minimaxi.backend.entity.SensorReading;
import com.minimaxi.backend.repository.MachineRepository;
import com.minimaxi.backend.repository.SensorReadingRepository;
import com.minimaxi.backend.service.SensorGeneratorService;
import com.minimaxi.backend.service.SensorSeedingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Periodically generates sensor readings (via SensorGeneratorService) and
 * persists them, so the frontend can fetch real history instead of
 * rebuilding it client-side from repeated polling.
 *
 * - Saves a fresh reading for every sensor of every machine every 30s.
 * - Purges readings older than RETENTION so the table doesn't grow forever.
 */
@Component
public class SensorReadingScheduler {

    private static final Logger log = LoggerFactory.getLogger(SensorReadingScheduler.class);

    // Keep in sync with the frontend's polling interval.
    private static final long SAVE_INTERVAL_MS = 30_000L;

    // How long readings are kept before cleanup removes them.
    private static final long RETENTION_HOURS = 6L;

    // Run cleanup every hour.
    private static final long CLEANUP_INTERVAL_MS = 60L * 60_000L;

    private final MachineRepository machineRepository;
    private final SensorGeneratorService sensorGenerator;
    private final SensorSeedingService sensorSeedingService;
    private final SensorReadingRepository sensorReadingRepository;

    public SensorReadingScheduler(MachineRepository machineRepository,
                                  SensorGeneratorService sensorGenerator,
                                  SensorSeedingService sensorSeedingService,
                                  SensorReadingRepository sensorReadingRepository) {
        this.machineRepository = machineRepository;
        this.sensorGenerator = sensorGenerator;
        this.sensorSeedingService = sensorSeedingService;
        this.sensorReadingRepository = sensorReadingRepository;
    }

    @Scheduled(fixedRate = SAVE_INTERVAL_MS)
    @Transactional
    public void captureReadings() {
        List<Machine> machines = machineRepository.findAll();

        for (Machine machine : machines) {
            try {
                captureForMachine(machine);
            } catch (Exception e) {
                // One bad machine shouldn't stop the rest from being captured.
                log.error("Failed to capture sensor readings for machine {}: {}",
                        machine.getAssetId(), e.getMessage(), e);
            }
        }
    }

    private void captureForMachine(Machine machine) {
        Map<Integer, Sensor> sensorsByIndex = sensorSeedingService.ensureSensorsExist(machine);
        List<Double> values = sensorGenerator.generate(machine);

        Instant now = Instant.now();

        for (int index : SensorMetaConstants.TRACKED_SENSOR_INDICES) {
            if (index >= values.size()) {
                continue;
            }

            Sensor sensor = sensorsByIndex.get(index);
            Double value = values.get(index);

            SensorReading reading = new SensorReading();
            reading.setSensor(sensor);
            reading.setValue(value);
            reading.setReadingTime(now);
            reading.setIngestedAt(now);
            sensorReadingRepository.save(reading);

            sensor.setCurrentValue(java.math.BigDecimal.valueOf(value));
            sensor.setLastReadingAt(now);
            // sensor is a managed entity within this @Transactional method,
            // so the update is flushed automatically; no explicit save needed.
        }
    }

    @Scheduled(fixedRate = CLEANUP_INTERVAL_MS)
    @Transactional
    public void purgeOldReadings() {
        Instant cutoff = Instant.now().minus(RETENTION_HOURS, ChronoUnit.HOURS);
        long deleted = sensorReadingRepository.deleteByReadingTimeBefore(cutoff);
        if (deleted > 0) {
            log.info("Purged {} sensor readings older than {}", deleted, cutoff);
        }
    }
}