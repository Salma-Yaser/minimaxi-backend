// =====================================================================
// AIInsightResponse.java
// =====================================================================
package com.minimaxi.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class
AIInsightResponse {
   /* {
        id: 3,
                machine_id: 6,
            machine_name: 'Industrial Engine #15',
            asset_id: 'ENGINE-015',
            insight: 'Temperature trending upward. Monitor closely for next 48 hours.',
            severity: 'warning',
            confidence: 78,
    }*/
    //the previous code snippet is an example of the expected JSON response format for this class

    private Long id;
    private Long machineId;
    private String machineName;
    private String assetId;
    private String insight;
    private String severity;
    private double confidence;

   /* private Long id;
    @JsonProperty("machine_id")
    private Long machineId;

    @JsonProperty("machine_name")
    private String machineName;

    @JsonProperty("asset_id")
    private String assetId;
    private String insight;
    private String severity;
    private double confidence;*/


    public AIInsightResponse(Long id, Long machineId, String machineName, String assetId,
                             String insight, String severity, double confidence) {
        this.id = id;
        this.machineId = machineId;
        this.machineName = machineName;
        this.assetId = assetId;
        this.insight = insight;
        this.severity = severity;
        this.confidence = confidence;
    }

    public Long getId() {
        return id;
    }

    public Long getMachineId() {
        return machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public String getAssetId() {
        return assetId;
    }

    public String getInsight() {
        return insight;
    }

    public String getSeverity() {
        return severity;
    }

    public double getConfidence() {
        return confidence;
    }
}







