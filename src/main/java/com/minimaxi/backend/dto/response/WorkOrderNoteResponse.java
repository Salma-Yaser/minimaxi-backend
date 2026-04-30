package com.minimaxi.backend.dto.response;

public class WorkOrderNoteResponse {
    private String id;
    private String content;
    private Long userId;
    private String createdAt;

    public WorkOrderNoteResponse(String id, String content, Long userId, String createdAt) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public Long getUserId() { return userId; }
    public String getCreatedAt() { return createdAt; }
}