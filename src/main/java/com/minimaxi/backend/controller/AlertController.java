package com.minimaxi.backend.controller;

import com.minimaxi.backend.dto.response.AlertResponse;
import com.minimaxi.backend.service.AlertService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "http://localhost:5173")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    // Frontend calls: GET /api/alerts  ✅
    // optional filters: ?severity=critical&acknowledged=false
    @GetMapping
    public List<AlertResponse> getAlerts(
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) Boolean acknowledged
    ) {
        return alertService.getAlerts(severity, acknowledged);
    }

    // Frontend calls: PUT /api/alerts/{id}/acknowledge  ✅
    @PutMapping("/{id}/acknowledge")
    public AlertResponse acknowledgeAlert(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String acknowledgedBy = body.getOrDefault("user", "Unknown");
        return alertService.acknowledgeAlert(id, acknowledgedBy);
    }
}