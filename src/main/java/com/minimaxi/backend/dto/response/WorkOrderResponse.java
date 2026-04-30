package com.minimaxi.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

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

    // parts_needed is missing
}