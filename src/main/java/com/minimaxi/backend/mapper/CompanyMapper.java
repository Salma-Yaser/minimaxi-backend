package com.minimaxi.backend.mapper;

import com.minimaxi.backend.dto.response.CompanySettingsResponse;
import com.minimaxi.backend.entity.Organization;

public class CompanyMapper {

    private CompanyMapper() {
    }

    public static CompanySettingsResponse toResponse(Organization organization) {
        return CompanySettingsResponse.builder()
                .id(organization.getId())
                .name(organization.getCompanyName())
                .logo(organization.getLogoUrl())
                .timezone(organization.getTimezone())
                .language("en")
                .serviceType(
                        organization.getRequestedService() != null
                                ? organization.getRequestedService().name().toLowerCase()
                                : null
                )
                .industry(organization.getIndustry())
                .setupCompleted(organization.getOnboardingCompleted())
                .build();
    }
}