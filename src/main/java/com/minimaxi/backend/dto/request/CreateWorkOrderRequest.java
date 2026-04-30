package com.minimaxi.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWorkOrderRequest {

    @JsonProperty("organization_id")
    private Long organizationId;

    @JsonProperty("machine_id")
    private Long machineId;

    @JsonProperty("issue_id")
    private Long issueId;

    @JsonProperty("created_by_user_id")
    private Long createdByUserId;

    @JsonProperty("assigned_to_user_id")
    private Long assignedToUserId;

    private String priority;
    private String status;

    @JsonProperty("due_date")
    private String dueDate;

    private String title;
    private String description;

    @JsonProperty("ai_suggested")
    private Boolean aiSuggested;
}