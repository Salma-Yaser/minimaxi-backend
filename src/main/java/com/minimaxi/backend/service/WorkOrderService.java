package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.request.AddWorkOrderNoteRequest;
import com.minimaxi.backend.dto.request.CreateWorkOrderRequest;
import com.minimaxi.backend.dto.request.UpdateWorkOrderRequest;
import com.minimaxi.backend.dto.response.WorkOrderNoteResponse;
import com.minimaxi.backend.dto.response.WorkOrderResponse;

import java.util.List;

public interface WorkOrderService {

    List<WorkOrderResponse> getAllWorkOrders(Long organizationId, String status, String priority, Long assignedTo);

    WorkOrderResponse getWorkOrderById(Long id);

    WorkOrderResponse createWorkOrder(CreateWorkOrderRequest request);

    WorkOrderResponse updateWorkOrder(Long id, UpdateWorkOrderRequest request);

    void deleteWorkOrder(Long id);

    WorkOrderNoteResponse addWorkOrderNote(Long id, AddWorkOrderNoteRequest request);
}