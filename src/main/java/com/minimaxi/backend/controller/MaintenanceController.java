package com.minimaxi.backend.controller;

import com.minimaxi.backend.config.JwtUtil;
import com.minimaxi.backend.dto.response.MaintenanceEventResponse;
import com.minimaxi.backend.service.MaintenanceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@CrossOrigin(origins = "http://localhost:5173")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;
    private final JwtUtil jwtUtil;

    public MaintenanceController(MaintenanceService maintenanceService , JwtUtil jwtUtil) {
        this.maintenanceService = maintenanceService;
        this.jwtUtil = jwtUtil;
    }

    // Frontend calls: GET /api/maintenance/events?month=2&year=2026  ✅
    @GetMapping("/events")
    public List<MaintenanceEventResponse> getMaintenanceEvents(
            HttpServletRequest request,
            @RequestParam int month,
            @RequestParam int year
    ) {
        String authHeader = request.getHeader("Authorization");
        Long orgId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            orgId = jwtUtil.extractOrganizationId(authHeader.substring(7));
        }
        return maintenanceService.getMaintenanceEvents(month, year, orgId);
    }
}