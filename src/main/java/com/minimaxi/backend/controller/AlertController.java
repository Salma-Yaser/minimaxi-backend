package com.minimaxi.backend.controller;

import com.minimaxi.backend.dto.response.AlertResponse;
import com.minimaxi.backend.service.AlertService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.minimaxi.backend.config.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "http://localhost:5173")
public class AlertController {

    private final AlertService alertService;
    private final JwtUtil jwtUtil;

    public AlertController(AlertService alertService, JwtUtil jwtUtil) {
        this.alertService = alertService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<AlertResponse> getAlerts(
            HttpServletRequest request,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) Boolean acknowledged
    ) {
        Long orgId = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            orgId = jwtUtil.extractOrganizationId(authHeader.substring(7));
        }
        return alertService.getAlerts(severity, acknowledged, orgId);
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