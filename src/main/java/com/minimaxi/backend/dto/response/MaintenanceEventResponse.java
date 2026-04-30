package com.minimaxi.backend.dto.response;

public class MaintenanceEventResponse {
    private String date;
    private String type;
    private long count;

    public MaintenanceEventResponse(String date, String type, long count) {
        this.date = date;
        this.type = type;
        this.count = count;
    }

    public String getDate() { return date; }
    public String getType() { return type; }
    public long getCount() { return count; }
}