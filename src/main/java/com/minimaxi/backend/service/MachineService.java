package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.request.CreateMachineRequest;
import com.minimaxi.backend.dto.request.UpdateMachineRequest;
import com.minimaxi.backend.dto.response.MachineResponse;
import com.minimaxi.backend.dto.response.SensorHistoryResponse;

import java.util.List;
import java.util.Map;

public interface MachineService {

    List<MachineResponse> getAllMachines(Long organizationId, String type, String location, String status, String search);

    MachineResponse getMachineById(Long id);

    MachineResponse createMachine(CreateMachineRequest request);

    MachineResponse updateMachine(Long id, UpdateMachineRequest request);
    List<SensorHistoryResponse> getSensorHistory(Long machineId, Integer hours);

    List<Map<String, Object>> getMachineIssues(Long machineId);
    List<Map<String, Object>> getMachineWorkOrders(Long machineId);
    List<Map<String, Object>> getMachineNotes(Long machineId);
    void deleteMachine(Long id, boolean force);
}