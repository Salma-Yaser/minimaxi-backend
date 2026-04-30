// =====================================================================
// ReportsResponse.java
// =====================================================================
package com.minimaxi.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class ReportsResponse {

    @JsonProperty("downtime_reduction")
    private double downtimeReduction;

    @JsonProperty("prediction_accuracy")
    private double predictionAccuracy;

    @JsonProperty("cost_savings")
    private double costSavings;

    @JsonProperty("preventive_vs_reactive")
    private Map<String, Integer> preventiveVsReactive;

    @JsonProperty("monthly_downtime")
    private List<MonthlyDowntime> monthlyDowntime;

    @JsonProperty("technician_performance")
    private List<TechnicianPerformance> technicianPerformance;

    public ReportsResponse(double downtimeReduction, double predictionAccuracy,
                           double costSavings, Map<String, Integer> preventiveVsReactive,
                           List<MonthlyDowntime> monthlyDowntime,
                           List<TechnicianPerformance> technicianPerformance) {
        this.downtimeReduction = downtimeReduction;
        this.predictionAccuracy = predictionAccuracy;
        this.costSavings = costSavings;
        this.preventiveVsReactive = preventiveVsReactive;
        this.monthlyDowntime = monthlyDowntime;
        this.technicianPerformance = technicianPerformance;
    }

    public double getDowntimeReduction() { return downtimeReduction; }
    public double getPredictionAccuracy() { return predictionAccuracy; }
    public double getCostSavings() { return costSavings; }
    public Map<String, Integer> getPreventiveVsReactive() { return preventiveVsReactive; }
    public List<MonthlyDowntime> getMonthlyDowntime() { return monthlyDowntime; }
    public List<TechnicianPerformance> getTechnicianPerformance() { return technicianPerformance; }

    // ─── Nested classes ───────────────────────────────────────────────────────

    public static class MonthlyDowntime {
        private String month;
        private double hours;

        public MonthlyDowntime(String month, double hours) {
            this.month = month;
            this.hours = hours;
        }

        public String getMonth() { return month; }
        public double getHours() { return hours; }
    }

    public static class TechnicianPerformance {
        private String name;
        private long completed;

        @JsonProperty("avg_time")
        private double avgTime;

        private double rating;

        public TechnicianPerformance(String name, long completed, double avgTime, double rating) {
            this.name = name;
            this.completed = completed;
            this.avgTime = avgTime;
            this.rating = rating;
        }

        public String getName() { return name; }
        public long getCompleted() { return completed; }
        public double getAvgTime() { return avgTime; }
        public double getRating() { return rating; }
    }
}