package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.response.*;

import java.util.List;

public interface DashboardService {

    DashboardStatsResponse getStats();

    List<HealthDistributionResponse> getHealthDistribution();

    List<FailureTrendResponse> getFailureTrend(String period);

    List<SensorTrendResponse> getSensorTrends();

    List<AIInsightResponse> getAIInsights();
}