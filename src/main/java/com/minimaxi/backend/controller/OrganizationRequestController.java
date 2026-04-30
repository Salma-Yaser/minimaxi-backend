package com.minimaxi.backend.controller;

import com.minimaxi.backend.dto.request.CreateAccessRequestRequest;
import com.minimaxi.backend.dto.response.AccessRequestResponse;
import com.minimaxi.backend.service.OrganizationRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/access-requests")
@CrossOrigin(origins = "http://localhost:5173")
public class OrganizationRequestController {

    private final OrganizationRequestService organizationRequestService;

    public OrganizationRequestController(OrganizationRequestService organizationRequestService) {
        this.organizationRequestService = organizationRequestService;
    }

    // GET /api/access-requests ✅
    @GetMapping
    public List<AccessRequestResponse> getAccessRequests() {
        return organizationRequestService.getAccessRequests();
    }

    // POST /api/access-requests ✅
    @PostMapping
    public AccessRequestResponse createAccessRequest(@RequestBody CreateAccessRequestRequest request) {
        return organizationRequestService.createAccessRequest(request);
    }

    // PUT /api/access-requests/{id}/approve ✅ — Admin يوافق ويتبعت إيميل
    @PutMapping("/{id}/approve")
    public Map<String, Object> approveAccessRequest(@PathVariable Long id) {
        return organizationRequestService.approveAccessRequest(id);
    }

    // PUT /api/access-requests/{id}/reject ✅ — Admin يرفض
    @PutMapping("/{id}/reject")
    public Map<String, Object> rejectAccessRequest(@PathVariable Long id) {
        return organizationRequestService.rejectAccessRequest(id);
    }
}