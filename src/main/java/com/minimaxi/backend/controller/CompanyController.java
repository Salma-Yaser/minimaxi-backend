package com.minimaxi.backend.controller;

import com.minimaxi.backend.dto.request.UpdateCompanySettingsRequest;
import com.minimaxi.backend.dto.response.CompanySettingsResponse;
import com.minimaxi.backend.service.CompanyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
@CrossOrigin(origins = "http://localhost:5173")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // Frontend calls: GET /api/company?companyId=1  ✅
    @GetMapping
    public CompanySettingsResponse getCompanySettings(@RequestParam("companyId") Long companyId) {
        return companyService.getCompanySettings(companyId);
    }

    // Frontend calls: PUT /api/company?companyId=1  ✅
    @PutMapping
    public CompanySettingsResponse updateCompanySettings(
            @RequestParam("companyId") Long companyId,
            @RequestBody UpdateCompanySettingsRequest request
    ) {
        return companyService.updateCompanySettings(companyId, request);
    }

    // Frontend calls: POST /api/company/complete-setup?companyId=1  ✅
    @PostMapping("/complete-setup")
    public CompanySettingsResponse completeSetup(@RequestParam("companyId") Long companyId) {
        return companyService.completeSetup(companyId);
    }
}