package com.minimaxi.backend.controller;

import com.minimaxi.backend.config.JwtUtil;
import com.minimaxi.backend.dto.response.ReportsResponse;
import com.minimaxi.backend.service.ReportsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class ReportsController {

    private final ReportsService reportsService;
    private final JwtUtil jwtUtil;

    public ReportsController(ReportsService reportsService, JwtUtil jwtUtil) {
        this.reportsService = reportsService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ReportsResponse getReportsData(HttpServletRequest request) {
        Long orgId = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            orgId = jwtUtil.extractOrganizationId(authHeader.substring(7));
        }
        return reportsService.getReportsData(orgId);
    }
}