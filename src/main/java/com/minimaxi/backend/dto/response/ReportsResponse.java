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

    @JsonProperty("mttr_mtbf")
    private MttrMtbf mttrMtbf;

    @JsonProperty("top_problem_machines")
    private List<TopProblemMachine> topProblemMachines;

    @JsonProperty("top_spare_parts")
    private List<TopSparePart> topSpareParts;

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

    // MTBF: متوسط الوقت بين الأعطال (بالساعات) لكل الماكينات مجمّعة
    // MTTR: متوسط وقت الإصلاح (بالساعات) لكل work order مقفولة
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MttrMtbf {
        @JsonProperty("mttr_hours")
        private Double mttrHours;

        @JsonProperty("mtbf_hours")
        private Double mtbfHours;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProblemMachine {
        @JsonProperty("machine_id")
        private Long machineId;

        @JsonProperty("machine_name")
        private String machineName;

        @JsonProperty("work_order_count")
        private Long workOrderCount;

        @JsonProperty("downtime_hours")
        private Double downtimeHours;

        private Double score;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopSparePart {
        private String name;

        @JsonProperty("usage_count")
        private Long usageCount;

        @JsonProperty("total_cost")
        private Double totalCost;
    }
}