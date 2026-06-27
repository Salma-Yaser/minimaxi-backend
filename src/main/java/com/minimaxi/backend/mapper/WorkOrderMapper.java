package com.minimaxi.backend.mapper;

import com.minimaxi.backend.dto.response.PersonRefResponse;
import com.minimaxi.backend.dto.response.WorkOrderResponse;
import com.minimaxi.backend.entity.WorkOrder;

import java.util.Map;

public class WorkOrderMapper {

    private WorkOrderMapper() {
    }

    public static WorkOrderResponse toResponse(WorkOrder workOrder) {
        return toResponse(workOrder, null, null);
    }

    public static WorkOrderResponse toResponse(WorkOrder workOrder, Boolean isRated) {
        return toResponse(workOrder, isRated, null);
    }

    public static WorkOrderResponse toResponse(WorkOrder workOrder, Boolean isRated, String sensorName) {
        return WorkOrderResponse.builder()
                .id(workOrder.getId())
                .woNumber("WO-" + workOrder.getId())
                .machineId(workOrder.getMachine() != null ? workOrder.getMachine().getId() : null)
                .machineName(workOrder.getMachine() != null ? workOrder.getMachine().getName() : null)
                .assetId(workOrder.getMachine() != null ? workOrder.getMachine().getAssetId() : null)
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
                .estimatedHours(workOrder.getEstimatedHours())
                .isRated(isRated)
                .sensorName(sensorName)
                .actualHours(
                        workOrder.getCompletion() != null && workOrder.getCompletion().getTimeSpentMinutes() != null
                                ? workOrder.getCompletion().getTimeSpentMinutes() / 60
                                : null
                )
                .actionTaken(
                        workOrder.getCompletion() != null ? workOrder.getCompletion().getActionTaken() : null
                )
                .rootCause(
                        workOrder.getCompletion() != null ? workOrder.getCompletion().getRootCause() : null
                )
                .hoursSpent(
                        workOrder.getCompletion() != null && workOrder.getCompletion().getTimeSpentMinutes() != null
                                ? workOrder.getCompletion().getTimeSpentMinutes() / 60
                                : null
                )
                .minutesSpent(
                        workOrder.getCompletion() != null && workOrder.getCompletion().getTimeSpentMinutes() != null
                                ? workOrder.getCompletion().getTimeSpentMinutes() % 60
                                : null
                )
                .additionalNotes(
                        workOrder.getCompletion() != null ? workOrder.getCompletion().getAdditionalNotes() : null
                )
                .spareParts(
                        workOrder.getCompletion() != null
                                ? workOrder.getCompletion().getSparePartsList().stream()
                                .map(sp -> {
                                    Map<String, Object> m = new java.util.LinkedHashMap<>();
                                    m.put("name", sp.getPartName());
                                    m.put("quantity", sp.getQuantity());
                                    m.put("cost", sp.getCost()); // ← زيدي السطر ده
                                    return m;
                                })
                                .toList()
                                : null
                )
                .build();
    }
}