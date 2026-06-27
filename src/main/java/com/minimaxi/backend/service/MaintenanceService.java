package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.response.MaintenanceEventResponse;

import java.util.List;
import java.util.Map;

public interface MaintenanceService {
    List<MaintenanceEventResponse> getMaintenanceEvents(int month, int year, Long organizationId);
    List<Map<String, Object>> getUpcomingMaintenance(Long orgId);
    List<Map<String, Object>> getExpectedMaintenance(Long orgId);
    List<Map<String, Object>> getLoadForecast(Long orgId, int weeks);
}