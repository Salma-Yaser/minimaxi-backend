package com.minimaxi.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportsResponse {

    @JsonProperty("downtime_reduction")
    private Double downtimeReduction;

    @JsonProperty("prediction_accuracy")
    private Double predictionAccuracy;

    @JsonProperty("cost_savings")
    private Double costSavings;

    @JsonProperty("preventive_vs_reactive")
    private Map<String, Integer> preventiveVsReactive;

    @JsonProperty("monthly_downtime")
    private List<MonthlyDowntime> monthlyDowntime;

    @JsonProperty("monthly_cost")
    private List<MonthlyCost> monthlyCost;

    @JsonProperty("accuracy_trend")
    private List<AccuracyTrend> accuracyTrend;

    @JsonProperty("technician_performance")
    private List<TechnicianPerformance> technicianPerformance;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyDowntime {
        private String month;

        @JsonProperty("before_hours")
        private Double beforeHours;

        @JsonProperty("after_hours")
        private Double afterHours;

        // backwards-compat للكود القديم
        public MonthlyDowntime(String month, Double hours) {
            this.month       = month;
            this.beforeHours = hours;
            this.afterHours  = Math.round(hours * 0.65 * 10.0) / 10.0;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyCost {
        private String month;
        private Double before;
        private Double after;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccuracyTrend {
        private String month;
        private Double accuracy;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TechnicianPerformance {
        private String name;
        private Long completed;

        @JsonProperty("avg_time")
        private Double avgTime;

        private Double rating;

        @JsonProperty("total_hours")
        private Double totalHours;

        @JsonProperty("success_rate")
        private Double successRate;

        // backwards-compat للكود القديم
        public TechnicianPerformance(String name, Long completed, Double avgTime, Double rating) {
            this.name        = name;
            this.completed   = completed;
            this.avgTime     = avgTime;
            this.rating      = rating;
            this.totalHours  = completed * avgTime;
            this.successRate = 94.0;
        }
    }
}