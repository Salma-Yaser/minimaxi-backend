package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.request.CreateMachineRequest;
import com.minimaxi.backend.dto.request.UpdateMachineRequest;
import com.minimaxi.backend.dto.response.MachineResponse;
import com.minimaxi.backend.dto.response.SensorHistoryResponse;

import java.util.List;

public interface MachineService {

    List<MachineResponse> getAllMachines(String type, String location, String status, String search);

    MachineResponse getMachineById(Long id);

    MachineResponse createMachine(CreateMachineRequest request);

    MachineResponse updateMachine(Long id, UpdateMachineRequest request);

    void deleteMachine(Long id);

    List<SensorHistoryResponse> getSensorHistory(Long machineId, Integer hours);
}