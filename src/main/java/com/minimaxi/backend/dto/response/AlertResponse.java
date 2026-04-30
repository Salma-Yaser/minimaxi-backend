package com.minimaxi.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AlertResponse {

    private Long id;
    private String type;
    private String severity;

    @JsonProperty("machine_id")
    private Long machineId;

    @JsonProperty("machine_name")
    private String machineName;

    @JsonProperty("asset_id")
    private String assetId;

    private String title;
    private String message;

    @JsonProperty("created_at")
    private String createdAt;

    private boolean acknowledged;

    @JsonProperty("acknowledged_by")
    private String acknowledgedBy;

    @JsonProperty("acknowledged_at")
    private String acknowledgedAt;

    public AlertResponse(Long id, String type, String severity,
                         Long machineId, String machineName, String assetId,
                         String title, String message, String createdAt,
                         boolean acknowledged, String acknowledgedBy, String acknowledgedAt) {
        this.id = id;
        this.type = type;
        this.severity = severity;
        this.machineId = machineId;
        this.machineName = machineName;
        this.assetId = assetId;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.acknowledged = acknowledged;
        this.acknowledgedBy = acknowledgedBy;
        this.acknowledgedAt = acknowledgedAt;
    }

    public Long getId() { return id; }
    public String getType() { return type; }
    public String getSeverity() { return severity; }
    public Long getMachineId() { return machineId; }
    public String getMachineName() { return machineName; }
    public String getAssetId() { return assetId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getCreatedAt() { return createdAt; }
    public boolean isAcknowledged() { return acknowledged; }
    public String getAcknowledgedBy() { return acknowledgedBy; }
    public String getAcknowledgedAt() { return acknowledgedAt; }
}