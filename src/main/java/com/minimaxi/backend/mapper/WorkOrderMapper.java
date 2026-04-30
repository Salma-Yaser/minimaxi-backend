package com.minimaxi.backend.mapper;

import com.minimaxi.backend.dto.response.PersonRefResponse;
import com.minimaxi.backend.dto.response.WorkOrderResponse;
import com.minimaxi.backend.entity.WorkOrder;

public class WorkOrderMapper {

    private WorkOrderMapper() {
    }

    public static WorkOrderResponse toResponse(WorkOrder workOrder) {
        return WorkOrderResponse.builder()
                .id(workOrder.getId())
                .woNumber("WO-" + workOrder.getId())
                .machineId(workOrder.getMachine() != null ? workOrder.getMachine().getId() : null)
                .machineName(workOrder.getMachine() != null ? workOrder.getMachine().getName() : null)
                .assetId(
                        workOrder.getMachine() != null
                                ? workOrder.getMachine().getAssetId()
                                : null
                )
                .title(workOrder.getTitle())
                .description(workOrder.getDescription())
                .priority(workOrder.getPriority() != null ? workOrder.getPriority().name().toLowerCase() : null)
                .status(workOrder.getStatus() != null ? workOrder.getStatus().name().toLowerCase() : null)
                .assignedTo(
                        workOrder.getAssignedToUser() != null
                                ? PersonRefResponse.builder()
                                .id(workOrder.getAssignedToUser().getId())
                                .name(workOrder.getAssignedToUser().getFullName())
                                .build()
                                : null
                )
                .createdBy(
                        workOrder.getCreatedByUser() != null
                                ? PersonRefResponse.builder()
                                .id(workOrder.getCreatedByUser().getId())
                                .name(workOrder.getCreatedByUser().getFullName())
                                .build()
                                : null
                )
                .createdAt(workOrder.getCreatedAt() != null ? workOrder.getCreatedAt().toString() : null)
                .dueDate(workOrder.getDueDate() != null ? workOrder.getDueDate().toString() : null)
                .completedAt(workOrder.getClosedAt() != null ? workOrder.getClosedAt().toString() : null)
                .estimatedHours(null)
                .actualHours(null)
                .build();
    }
}