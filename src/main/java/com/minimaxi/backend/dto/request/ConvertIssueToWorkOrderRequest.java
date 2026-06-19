package com.minimaxi.backend.dto.request;

import lombok.Data;

@Data
public class ConvertIssueToWorkOrderRequest {

    // مطلوب: اليوزر اللي بيعمل الـ convert (هيبقى createdByUser بتاع الـ WorkOrder)
    private Long createdByUserId;

    // اختياري
    private Long assignedToUserId;
    private String priority;     // "low" | "medium" | "high" ... زي ما متوقع في WorkOrderPriority
    private String dueDate;      // "yyyy-MM-dd"
    private Integer estimatedHours;
}