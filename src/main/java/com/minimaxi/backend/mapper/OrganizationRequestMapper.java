package com.minimaxi.backend.mapper;

import com.minimaxi.backend.dto.response.AccessRequestResponse;
import com.minimaxi.backend.entity.OrganizationRequest;

public class OrganizationRequestMapper {

    private OrganizationRequestMapper() {
    }

    public static AccessRequestResponse toResponse(OrganizationRequest request) {
        return AccessRequestResponse.builder()
                .id(request.getId())
                .companyName(request.getCompanyName())
                .industry(request.getIndustry())
                .contactPersonName(request.getContactPersonName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .requestedService(
                        request.getRequestedService() != null
                                ? request.getRequestedService().name().toLowerCase()
                                : null
                )
                .status(
                        request.getStatus() != null
                                ? request.getStatus().name().toLowerCase()
                                : null
                )
                .createdAt(
                        request.getCreatedAt() != null
                                ? request.getCreatedAt().toString()
                                : null
                )
                .build();
    }
}