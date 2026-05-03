package com.minimaxi.backend.controller;

import com.minimaxi.backend.config.JwtUtil;
import com.minimaxi.backend.dto.request.CreateMachineRequest;
import com.minimaxi.backend.dto.request.UpdateMachineRequest;
import com.minimaxi.backend.dto.response.MachineResponse;
import com.minimaxi.backend.dto.response.SensorHistoryResponse;
import com.minimaxi.backend.service.MachineService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/machines")
@CrossOrigin(origins = "*")
public class MachineController {

    private final MachineService machineService;
    private final JwtUtil jwtUtil;

    public MachineController(MachineService machineService, JwtUtil jwtUtil) {
        this.machineService = machineService;
        this.jwtUtil = jwtUtil;
    }

    private Long extractOrgId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.extractOrganizationId(token);
        }
        return null;
    }

    @GetMapping
    public List<MachineResponse> getAllMachines(
            HttpServletRequest request,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search
    ) {
        Long orgId = extractOrgId(request);
        return machineService.getAllMachines(orgId, type, location, status, search);
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