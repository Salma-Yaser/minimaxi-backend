package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.dto.request.UpdateCompanySettingsRequest;
import com.minimaxi.backend.dto.response.CompanySettingsResponse;
import com.minimaxi.backend.entity.Organization;
import com.minimaxi.backend.mapper.CompanyMapper;
import com.minimaxi.backend.repository.OrganizationRepository;
import com.minimaxi.backend.service.CompanyService;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final OrganizationRepository organizationRepository;

    public CompanyServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public CompanySettingsResponse getCompanySettings(Long companyId) {
        Organization organization = organizationRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + companyId));
        return CompanyMapper.toResponse(organization);
    }

    @Override
    public CompanySettingsResponse updateCompanySettings(Long companyId, UpdateCompanySettingsRequest request) {
        Organization organization = organizationRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + companyId));

        if (request.getName() != null) {
            organization.setCompanyName(request.getName());
        }
        if (request.getLogo() != null) {
            organization.setLogoUrl(request.getLogo());
        }
        if (request.getTimezone() != null) {
            organization.setTimezone(request.getTimezone());
        }
        if (request.getIndustry() != null) {
            organization.setIndustry(request.getIndustry());
        }

        Organization saved = organizationRepository.save(organization);
        return CompanyMapper.toResponse(saved);
    }

    @Override
    public CompanySettingsResponse completeSetup(Long companyId) {
        Organization organization = organizationRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + companyId));

        organization.setOnboardingCompleted(true);

        Organization saved = organizationRepository.save(organization);
        return CompanyMapper.toResponse(saved);
    }
}