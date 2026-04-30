// =====================================================================
// DashboardStatsResponse.java
// =====================================================================
package com.minimaxi.backend.dto.response;

public class DashboardStatsResponse {
    private long totalAssets;
    private long healthy;
    private long warning;
    private long critical;
    private long activeWorkOrders;
    private long predictedFailures;
    private double uptimePercentage;
    private double mtbf;
    private double mttr;

    public DashboardStatsResponse(long totalAssets, long healthy, long warning, long critical,
                                  long activeWorkOrders, long predictedFailures,
                                  double uptimePercentage, double mtbf, double mttr) {
        this.totalAssets = totalAssets;
        this.healthy = healthy;
        this.warning = warning;
        this.critical = critical;
        this.activeWorkOrders = activeWorkOrders;
        this.predictedFailures = predictedFailures;
        this.uptimePercentage = uptimePercentage;
        this.mtbf = mtbf;
        this.mttr = mttr;
    }

    public long getTotalAssets() { return totalAssets; }
    public long getHealthy() { return healthy; }
    public long getWarning() { return warning; }
    public long getCritical() { return critical; }
    public long getActiveWorkOrders() { return activeWorkOrders; }
    public long getPredictedFailures() { return predictedFailures; }
    public double getUptimePercentage() { return uptimePercentage; }
    public double getMtbf() { return mtbf; }
    public double getMttr() { return mttr; }
}






