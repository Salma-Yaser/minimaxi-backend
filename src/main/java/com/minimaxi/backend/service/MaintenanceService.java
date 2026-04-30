package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.response.MaintenanceEventResponse;

import java.util.List;

public interface MaintenanceService {
    List<MaintenanceEventResponse> getMaintenanceEvents(int month, int year);
}