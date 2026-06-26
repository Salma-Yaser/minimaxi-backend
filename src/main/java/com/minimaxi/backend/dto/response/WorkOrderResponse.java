package com.minimaxi.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class WorkOrderResponse {
    private Long id;

    @JsonProperty("wo_number")
    private String woNumber;

    @JsonProperty("machine_id")
    private Long machineId;

    @JsonProperty("machine_name")
    private String machineName;

    @JsonProperty("asset_id")
    private String assetId;

    private String title;
    private String description;
    private String priority;
    private String status;

    @JsonProperty("assigned_to")
    private PersonRefResponse assignedTo;

    @JsonProperty("created_by")
    private PersonRefResponse createdBy;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("due_date")
    private String dueDate;

    //this field not in mock data
    @JsonProperty("completed_at")
    private String completedAt;

    @JsonProperty("estimated_hours")
    private Integer estimatedHours;

    @JsonProperty("actual_hours")
    private Integer actualHours;

    // true لو الـ work order دي اتقيّمت قبل كده (عبر POST /api/work-orders/{id}/rate)
    @JsonProperty("is_rated")
    private Boolean isRated;

    @JsonProperty("sensor_name")
    private String sensorName;
    // parts_needed is missing


    @JsonProperty("action_taken")
    private String actionTaken;

    @JsonProperty("root_cause")
    private String rootCause;

    @JsonProperty("hours_spent")
    private Integer hoursSpent;

    @JsonProperty("minutes_spent")
    private Integer minutesSpent;

    @JsonProperty("additional_notes")
    private String additionalNotes;

    @JsonProperty("spare_parts")
    private List<Map<String, Object>> spareParts;
}