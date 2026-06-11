package com.minimaxi.backend.controller;

import com.minimaxi.backend.config.JwtUtil;
import com.minimaxi.backend.dto.response.*;
import com.minimaxi.backend.service.DashboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    private final DashboardService dashboardService;
    private final JwtUtil jwtUtil;

    public DashboardController(DashboardService dashboardService, JwtUtil jwtUtil) {
        this.dashboardService = dashboardService;
        this.jwtUtil = jwtUtil;
    }

    // helper يستخرج الـ organizationId من الـ Authorization header
    private Long extractOrgId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = header.substring(7); // يشيل "Bearer "
        return jwtUtil.extractOrganizationId(token);
    }

    @GetMapping("/stats")
    public DashboardStatsResponse getStats(HttpServletRequest request) {
        return dashboardService.getStats(extractOrgId(request));
    }

    @GetMapping("/health-distribution")
    public List<HealthDistributionResponse> getHealthDistribution(HttpServletRequest request) {
        return dashboardService.getHealthDistribution(extractOrgId(request));
    }

    @GetMapping("/failure-trend")
    public List<FailureTrendResponse> getFailureTrend(
            @RequestParam(required = false, defaultValue = "monthly") String period,
            HttpServletRequest request
    ) {
        return dashboardService.getFailureTrend(period, extractOrgId(request));
    }

    @GetMapping("/sensor-trends")
    public List<SensorTrendResponse> getSensorTrends(HttpServletRequest request) {
        return dashboardService.getSensorTrends(extractOrgId(request));
    }

    @GetMapping("/ai-insights")
    public List<AIInsightResponse> getAIInsights(HttpServletRequest request) {
        return dashboardService.getAIInsights(extractOrgId(request));
    }
}