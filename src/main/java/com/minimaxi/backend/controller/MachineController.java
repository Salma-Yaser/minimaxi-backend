package com.minimaxi.backend.controller;

import com.minimaxi.backend.dto.request.CreateMachineRequest;
import com.minimaxi.backend.dto.request.UpdateMachineRequest;
import com.minimaxi.backend.dto.response.MachineResponse;
import com.minimaxi.backend.dto.response.SensorHistoryResponse;
import com.minimaxi.backend.service.MachineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/machines")
@CrossOrigin(origins = "http://localhost:5173")
public class MachineController {

    private final MachineService machineService;

    public MachineController(MachineService machineService) {
        this.machineService = machineService;
    }

    @GetMapping
    public List<MachineResponse> getAllMachines(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search
    ) {
        return machineService.getAllMachines(type, location, status, search);
    }

    @GetMapping("/{id}")
    public MachineResponse getMachineById(@PathVariable Long id) {
        return machineService.getMachineById(id);
    }

    @PostMapping
    public MachineResponse createMachine(@RequestBody CreateMachineRequest request) {
        return machineService.createMachine(request);
    }

    @PutMapping("/{id}")
    public MachineResponse updateMachine(
            @PathVariable Long id,
            @RequestBody UpdateMachineRequest request
    ) {
        return machineService.updateMachine(id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteMachine(@PathVariable Long id) {
        machineService.deleteMachine(id);
        return Map.of("success", true);
    }

    @GetMapping("/{id}/sensor-history")
    public List<SensorHistoryResponse> getSensorHistory(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "24") Integer hours
    ) {
        return machineService.getSensorHistory(id, hours);
    }
}