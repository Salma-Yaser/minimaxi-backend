
// =====================================================================
// HealthDistributionResponse.java
// =====================================================================
package com.minimaxi.backend.dto.response;

public class HealthDistributionResponse {
    private String name;
    private long value;
    private String color;

    public HealthDistributionResponse(String name, long value, String color) {
        this.name = name;
        this.value = value;
        this.color = color;
    }

    public String getName() { return name; }
    public long getValue() { return value; }
    public String getColor() { return color; }
}

