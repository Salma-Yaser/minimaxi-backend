package com.minimaxi.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private String type;
    private String title;
    private String message;
    private Boolean read;

    @JsonProperty("created_at")
    private String createdAt;

    private String severity;

    @JsonProperty("machine_id")
    private Long machineId;

    @JsonProperty("machine_name")
    private String machineName;

    @JsonProperty("work_order_id")
    private Long workOrderId;
}