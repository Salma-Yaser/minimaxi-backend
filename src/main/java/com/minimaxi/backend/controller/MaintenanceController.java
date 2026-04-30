package com.minimaxi.backend.controller;

import com.minimaxi.backend.dto.response.MaintenanceEventResponse;
import com.minimaxi.backend.service.MaintenanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@CrossOrigin(origins = "http://localhost:5173")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    // Frontend calls: GET /api/maintenance/events?month=2&year=2026  ✅
    @GetMapping("/events")
    public List<MaintenanceEventResponse> getMaintenanceEvents(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return maintenanceService.getMaintenanceEvents(month, year);
    }
}