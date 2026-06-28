package com.minimaxi.backend.dto.request;

public class AskRequest {
    private String message;
    private Long conversationId; // null = ابدأ محادثة جديدة
    private Long machineId;
    private Long workOrderId;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
    public Long getMachineId() { return machineId; }
    public void setMachineId(Long machineId) { this.machineId = machineId; }
    public Long getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(Long workOrderId) { this.workOrderId = workOrderId; }
}