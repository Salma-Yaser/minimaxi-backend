package com.minimaxi.backend.controller;

import com.minimaxi.backend.config.JwtUtil;
import com.minimaxi.backend.dto.request.*;
import com.minimaxi.backend.dto.response.WorkOrderNoteResponse;
import com.minimaxi.backend.dto.response.WorkOrderResponse;
import com.minimaxi.backend.service.WorkOrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/work-orders")
@CrossOrigin(origins = "*")
public class WorkOrderController {

    private final WorkOrderService workOrderService;
    private final JwtUtil jwtUtil;

    public WorkOrderController(WorkOrderService workOrderService, JwtUtil jwtUtil) {
        this.workOrderService = workOrderService;
        this.jwtUtil = jwtUtil;
    }

    private Long extractOrgId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return jwtUtil.extractOrganizationId(authHeader.substring(7));
        }
        return null;
    }

    @GetMapping
    public List<WorkOrderResponse> getAllWorkOrders(
            HttpServletRequest request,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long assigned_to
    ) {
        Long orgId = extractOrgId(request);
        return workOrderService.getAllWorkOrders(orgId, status, priority, assigned_to);
    }

    @GetMapping("/{id}")
    public WorkOrderResponse getWorkOrderById(@PathVariable Long id) {
        return workOrderService.getWorkOrderById(id);
    }

    @PostMapping
    public WorkOrderResponse createWorkOrder(@RequestBody CreateWorkOrderRequest request) {
        return workOrderService.createWorkOrder(request);
    }

    @PutMapping("/{id}")
    public WorkOrderResponse updateWorkOrder(
            @PathVariable Long id,
            @RequestBody UpdateWorkOrderRequest request
    ) {
        return workOrderService.updateWorkOrder(id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteWorkOrder(@PathVariable Long id) {
        workOrderService.deleteWorkOrder(id);
        return Map.of("success", true);
    }

    @PostMapping("/{id}/notes")
    public WorkOrderNoteResponse addWorkOrderNote(
            @PathVariable Long id,
            @RequestBody AddWorkOrderNoteRequest request
    ) {
        return workOrderService.addWorkOrderNote(id, request);
    }

    @PostMapping("/{id}/complete")
    public Map<String, Object> completeWorkOrder(
            @PathVariable Long id,
            @RequestBody CompleteWorkOrderRequest request
    ) {
        workOrderService.completeWorkOrder(id, request);
        return Map.of("success", true, "message", "Work order " + id + " marked as completed.");
    }

    @PostMapping("/from-issue/{issueId}")
    public WorkOrderResponse convertIssueToWorkOrder(
            @PathVariable Long issueId,
            @RequestBody ConvertIssueToWorkOrderRequest request) {
        return workOrderService.convertIssueToWorkOrder(issueId, request);
    }

}