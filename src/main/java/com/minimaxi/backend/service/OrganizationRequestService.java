package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.request.CreateAccessRequestRequest;
import com.minimaxi.backend.dto.response.AccessRequestResponse;

import java.util.List;
import java.util.Map;

public interface OrganizationRequestService {
    List<AccessRequestResponse> getAccessRequests();
    AccessRequestResponse createAccessRequest(CreateAccessRequestRequest request);
    Map<String, Object> approveAccessRequest(Long id);
    Map<String, Object> rejectAccessRequest(Long id);
}