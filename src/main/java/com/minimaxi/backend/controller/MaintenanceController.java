package com.minimaxi.backend.controller;

import com.minimaxi.backend.config.JwtUtil;
import com.minimaxi.backend.dto.response.MaintenanceEventResponse;
import com.minimaxi.backend.service.MaintenanceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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


    @GetMapping("/assets/upcoming")
    public List<Map<String, Object>> getUpcomingMaintenance(HttpServletRequest request) {
        Long orgId = extractOrgId(request);
        return maintenanceService.getUpcomingMaintenance(orgId);
    }

    @GetMapping("/assets/expected")
    public List<Map<String, Object>> getExpectedMaintenance(HttpServletRequest request) {
        Long orgId = extractOrgId(request);
        return maintenanceService.getExpectedMaintenance(orgId);
    }

    @GetMapping("/load-forecast")
    public List<Map<String, Object>> getLoadForecast(
            HttpServletRequest request,
            @RequestParam(defaultValue = "4") int weeks
    ) {
        Long orgId = extractOrgId(request);
        return maintenanceService.getLoadForecast(orgId, weeks);
    }

    // أضيفي الـ helper method لو مش موجودة
    private Long extractOrgId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return jwtUtil.extractOrganizationId(authHeader.substring(7));
        }
        return null;
    }
}