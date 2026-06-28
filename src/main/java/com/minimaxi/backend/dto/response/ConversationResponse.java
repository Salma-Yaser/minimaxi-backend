package com.minimaxi.backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class ConversationResponse {
    private Long id;
    private Long machineId;
    private String machineName;
    private Long workOrderId;
    private String workOrderTitle;
    private String status;
    private Instant createdAt;
    private Instant lastMessageAt;
}