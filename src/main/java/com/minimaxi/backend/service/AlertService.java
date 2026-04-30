package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.response.AlertResponse;

import java.util.List;
import java.util.Map;

public interface AlertService {

    List<AlertResponse> getAlerts(String severity, Boolean acknowledged);

    AlertResponse acknowledgeAlert(Long id, String acknowledgedBy);
}