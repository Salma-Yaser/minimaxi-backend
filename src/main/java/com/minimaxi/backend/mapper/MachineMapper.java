package com.minimaxi.backend.mapper;

import com.minimaxi.backend.dto.response.MachinePredictionResponse;
import com.minimaxi.backend.dto.response.MachineResponse;
import com.minimaxi.backend.entity.Machine;

import java.util.Map;


public class MachineMapper {

    private MachineMapper() {
    }

    public static MachineResponse toResponse(Machine machine) {
        return MachineResponse.builder()
                .id(machine.getId())
                .assetId(machine.getAssetId())
                .name(machine.getName())
                .type(
                        machine.getAssetType() != null
                                ? machine.getAssetType().getName()
                                : machine.getMachineType()
                )
                .location(machine.getLocation())
                .serialNumber(machine.getSerialNumber())
                .manufacturer(null)
                .model(null)
                .installationDate(
                        machine.getInstallationDate() != null
                                ? machine.getInstallationDate().toString()
                                : null
                )
                .criticality(
                        machine.getCriticality() != null
                                ? machine.getCriticality().name().toLowerCase()
                                : null
                )
                .status(
                        machine.getStatus() != null
                                ? machine.getStatus().name().toLowerCase()
                                : null
                )
                .lastMaintenance(null)
                .sensors(Map.of())
                .prediction(
                        MachinePredictionResponse.builder()
                                .failure_probability(0.0)
                                .rul(0.0)
                                .ttf("N/A")
                                .status("healthy")
                                .recommendation("Prediction not loaded yet")
                                .build()
                )
                .build();
    }
}