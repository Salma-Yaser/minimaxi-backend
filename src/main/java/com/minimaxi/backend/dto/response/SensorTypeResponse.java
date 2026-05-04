package com.minimaxi.backend.dto.response;

public class SensorTypeResponse {
    private Long id;
    private String name;
    private String unit;

    public SensorTypeResponse(Long id, String name, String unit) {
        this.id = id;
        this.name = name;
        this.unit = unit;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getUnit() { return unit; }
}