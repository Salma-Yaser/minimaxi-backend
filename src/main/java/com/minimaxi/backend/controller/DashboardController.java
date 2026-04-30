package com.minimaxi.backend.controller;

import com.minimaxi.backend.dto.response.*;
import com.minimaxi.backend.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // Frontend calls: GET /api/dashboard/stats  ✅
    @GetMapping("/stats")
    public DashboardStatsResponse getStats() {
        return dashboardService.getStats();
    }

    // Frontend calls: GET /api/dashboard/health-distribution  ✅
    @GetMapping("/health-distribution")
    public List<HealthDistributionResponse> getHealthDistribution() {
        return dashboardService.getHealthDistribution();
    }

    // Frontend calls: GET /api/dashboard/failure-trend?period=monthly  ✅
    @GetMapping("/failure-trend")
    public List<FailureTrendResponse> getFailureTrend(
            @RequestParam(required = false, defaultValue = "monthly") String period
    ) {
        return dashboardService.getFailureTrend(period);
    }

    // Frontend calls: GET /api/dashboard/sensor-trends  ✅
    @GetMapping("/sensor-trends")
    public List<SensorTrendResponse> getSensorTrends() {
        return dashboardService.getSensorTrends();
    }

    // Frontend calls: GET /api/dashboard/ai-insights  ✅
    @GetMapping("/ai-insights")
    public List<AIInsightResponse> getAIInsights() {
        return dashboardService.getAIInsights();
    }
}