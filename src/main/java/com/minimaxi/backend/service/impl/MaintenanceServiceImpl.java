package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.dto.response.MaintenanceEventResponse;
import com.minimaxi.backend.enums.WorkOrderPriority;
import com.minimaxi.backend.repository.WorkOrderRepository;
import com.minimaxi.backend.service.MaintenanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private final WorkOrderRepository workOrderRepository;

    public MaintenanceServiceImpl(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
    }

    @Override
    @Transactional
    public List<MaintenanceEventResponse> getMaintenanceEvents(int month, int year) {
        // بنجيب work orders اللي الـ due_date بتاعها في الـ month/year المطلوب
        var workOrders = workOrderRepository.findAll().stream()
                .filter(wo -> wo.getDueDate() != null
                        && wo.getDueDate().getMonthValue() == month
                        && wo.getDueDate().getYear() == year)
                .toList();

        // بنعمل group بالـ date والـ type
        Map<String, Map<String, Long>> grouped = new LinkedHashMap<>();

        for (var wo : workOrders) {
            String date = wo.getDueDate().toString(); // "YYYY-MM-DD"

            // نحدد الـ type من الـ priority
            String type = switch (wo.getPriority()) {
                case CRITICAL, HIGH -> "critical";
                case MEDIUM         -> "warning";
                default             -> "scheduled";
            };

            grouped
                    .computeIfAbsent(date, k -> new LinkedHashMap<>())
                    .merge(type, 1L, Long::sum);
        }

        // نحول لـ list من MaintenanceEventResponse
        List<MaintenanceEventResponse> result = new ArrayList<>();
        for (var dateEntry : grouped.entrySet()) {
            for (var typeEntry : dateEntry.getValue().entrySet()) {
                result.add(new MaintenanceEventResponse(
                        dateEntry.getKey(),
                        typeEntry.getKey(),
                        typeEntry.getValue()
                ));
            }
        }

        return result;
    }
}