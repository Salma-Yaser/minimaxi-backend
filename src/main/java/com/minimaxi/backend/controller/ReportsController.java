package com.minimaxi.backend.controller;

import com.minimaxi.backend.dto.response.ReportsResponse;
import com.minimaxi.backend.service.ReportsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class ReportsController {

    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    // Frontend calls: GET /api/reports  ✅
    @GetMapping
    public ReportsResponse getReportsData() {
        return reportsService.getReportsData();
    }
}