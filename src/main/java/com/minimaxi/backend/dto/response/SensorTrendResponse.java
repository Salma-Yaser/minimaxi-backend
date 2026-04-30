// =====================================================================
// SensorTrendResponse.java
// =====================================================================
package com.minimaxi.backend.dto.response;

public class SensorTrendResponse {
    private String time;
    private Double temperature;
    private Double vibration;
    private Double pressure;

    public SensorTrendResponse(String time, Double temperature, Double vibration, Double pressure) {
        this.time = time;
        this.temperature = temperature;
        this.vibration = vibration;
        this.pressure = pressure;
    }

    public String getTime() { return time; }
    public Double getTemperature() { return temperature; }
    public Double getVibration() { return vibration; }
    public Double getPressure() { return pressure; }
}