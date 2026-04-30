// =====================================================================
// FailureTrendResponse.java
// =====================================================================
package com.minimaxi.backend.dto.response;

public class FailureTrendResponse {
    private String label;
    private double probability;

    public FailureTrendResponse(String label, double probability) {
        this.label = label;
        this.probability = probability;
    }

    public String getLabel() { return label; }
    public double getProbability() { return probability; }
}