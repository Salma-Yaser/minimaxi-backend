package com.minimaxi.backend.service;

import com.minimaxi.backend.config.SensorMetaConstants;
import com.minimaxi.backend.entity.Machine;
import com.minimaxi.backend.entity.Organization;
import com.minimaxi.backend.entity.Sensor;
import com.minimaxi.backend.entity.SensorType;
import com.minimaxi.backend.repository.SensorRepository;
import com.minimaxi.backend.repository.SensorTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Makes sure that, before we try to save a SensorReading, the Sensor (and its
 * SensorType) actually exist in the DB. Safe to call repeatedly: it only
 * creates what's missing.
 */
@Service
public class SensorSeedingService {

    private final SensorTypeRepository sensorTypeRepository;
    private final SensorRepository sensorRepository;

    public SensorSeedingService(SensorTypeRepository sensorTypeRepository,
                                SensorRepository sensorRepository) {
        this.sensorTypeRepository = sensorTypeRepository;
        this.sensorRepository = sensorRepository;
    }

    /**
     * Ensures the machine has Sensor rows (and matching SensorType rows on
     * its organization) for the TRACKED sensors only (Temperature, Pressure,
     * Vibration today). Returns a map of sensorIndex -> Sensor so callers
     * can look up by the same index used in SensorGeneratorService output.
     */
    @Transactional
    public Map<Integer, Sensor> ensureSensorsExist(Machine machine) {
        Organization organization = machine.getOrganization();

        Map<String, SensorType> typesByName = sensorTypeRepository
                .findByOrganizationId(organization.getId())
                .stream()
                .collect(Collectors.toMap(SensorType::getName, t -> t, (a, b) -> a));

        // Create any missing SensorType (for the tracked sensors only).
        for (int index : SensorMetaConstants.TRACKED_SENSOR_INDICES) {
            String name = SensorMetaConstants.NAMES[index];
            if (!typesByName.containsKey(name)) {
                SensorType type = new SensorType();
                type.setOrganization(organization);
                type.setName(name);
                type.setUnit(SensorMetaConstants.UNITS[index]);
                type.setCreatedAt(Instant.now());
                type = sensorTypeRepository.save(type);
                typesByName.put(name, type);
            }
        }

        Map<String, Sensor> sensorsByRef = sensorRepository
                .findByMachineId(machine.getId())
                .stream()
                .collect(Collectors.toMap(Sensor::getExternalRef, s -> s, (a, b) -> a));

        Map<Integer, Sensor> result = new LinkedHashMap<>();

        for (int index : SensorMetaConstants.TRACKED_SENSOR_INDICES) {
            String ref = SensorMetaConstants.externalRef(index);
            Sensor sensor = sensorsByRef.get(ref);

            if (sensor == null) {
                sensor = new Sensor();
                sensor.setOrganization(organization);
                sensor.setMachine(machine);
                sensor.setSensorType(typesByName.get(SensorMetaConstants.NAMES[index]));
                sensor.setExternalRef(ref);
                sensor.setCreatedAt(Instant.now());
                sensor = sensorRepository.save(sensor);
            }

            result.put(index, sensor);
        }

        return result;
    }
}