package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.dto.request.CreateAccessRequestRequest;
import com.minimaxi.backend.dto.response.AccessRequestResponse;
import com.minimaxi.backend.entity.OrganizationRequest;
import com.minimaxi.backend.enums.RequestStatus;
import com.minimaxi.backend.enums.RequestedService;
import com.minimaxi.backend.mapper.OrganizationRequestMapper;
import com.minimaxi.backend.repository.OrganizationRequestRepository;
import com.minimaxi.backend.service.EmailService;
import com.minimaxi.backend.service.OrganizationRequestService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class OrganizationRequestServiceImpl implements OrganizationRequestService {

    private final OrganizationRequestRepository organizationRequestRepository;
    private final EmailService emailService;

    public OrganizationRequestServiceImpl(
            OrganizationRequestRepository organizationRequestRepository,
            EmailService emailService
    ) {
        this.organizationRequestRepository = organizationRequestRepository;
        this.emailService = emailService;
    }

    @Override
    public List<AccessRequestResponse> getAccessRequests() {
        return organizationRequestRepository.findAll()
                .stream()
                .map(OrganizationRequestMapper::toResponse)
                .toList();
    }

    @Override
    public AccessRequestResponse createAccessRequest(CreateAccessRequestRequest request) {
        OrganizationRequest entity = new OrganizationRequest();
        entity.setCompanyName(request.getCompanyName());
        entity.setIndustry(request.getIndustry());
        entity.setContactPersonName(request.getContactPersonName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setRequestedService(
                request.getRequestedService() != null
                        ? RequestedService.valueOf(request.getRequestedService().toUpperCase())
                        : RequestedService.BOTH
        );
        entity.setStatus(RequestStatus.PENDING);
        entity.setCreatedAt(Instant.now());

        OrganizationRequest saved = organizationRequestRepository.save(entity);
        return OrganizationRequestMapper.toResponse(saved);
    }

    @Override
    public Map<String, Object> approveAccessRequest(Long id) {
        OrganizationRequest request = organizationRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Access request not found with id: " + id));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Request is already " + request.getStatus().name().toLowerCase());
        }

        request.setStatus(RequestStatus.APPROVED);
        request.setReviewedAt(Instant.now());
        organizationRequestRepository.save(request);

        // بعت الإيميل
        emailService.sendActivationEmail(
                request.getEmail(),
                request.getContactPersonName(),
                request.getId()
        );

        return Map.of(
                "success", true,
                "message", "Request approved and activation email sent to " + request.getEmail()
        );
    }

    @Override
    public Map<String, Object> rejectAccessRequest(Long id) {
        OrganizationRequest request = organizationRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Access request not found with id: " + id));

        request.setStatus(RequestStatus.REJECTED);
        request.setReviewedAt(Instant.now());
        organizationRequestRepository.save(request);

        return Map.of(
                "success", true,
                "message", "Request rejected"
        );
    }
}