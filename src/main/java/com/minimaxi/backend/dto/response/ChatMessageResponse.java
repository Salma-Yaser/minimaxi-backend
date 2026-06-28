package com.minimaxi.backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class ChatMessageResponse {
    private Long id;
    private String sender; // "USER" or "BOT"
    private String message;
    private Instant createdAt;
}