package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.response.*;

import java.util.List;

public interface DashboardService {



    DashboardStatsResponse getStats(Long organizationId);
    List<HealthDistributionResponse> getHealthDistribution(Long organizationId);
    List<FailureTrendResponse> getFailureTrend(String period, Long organizationId);
    List<SensorTrendResponse> getSensorTrends(Long organizationId);
    List<AIInsightResponse> getAIInsights(Long organizationId);
}