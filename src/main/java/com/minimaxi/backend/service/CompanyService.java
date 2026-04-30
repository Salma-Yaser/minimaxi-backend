package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.request.UpdateCompanySettingsRequest;
import com.minimaxi.backend.dto.response.CompanySettingsResponse;

public interface CompanyService {
    CompanySettingsResponse getCompanySettings(Long companyId);
    CompanySettingsResponse updateCompanySettings(Long companyId, UpdateCompanySettingsRequest request);
    CompanySettingsResponse completeSetup(Long companyId);
}