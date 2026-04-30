// =====================================================================
// AIModelInfoResponse.java
// =====================================================================
package com.minimaxi.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class AIModelInfoResponse {
    private Long id;

    @JsonProperty("model_name")
    private String modelName;

    private String version;

    @JsonProperty("last_training_date")
    private String lastTrainingDate;

    private String notes;

    @JsonProperty("features_used")
    private Map<String, Object> featuresUsed;

    public AIModelInfoResponse(Long id, String modelName, String version,
                               String lastTrainingDate, String notes,
                               Map<String, Object> featuresUsed) {
        this.id = id;
        this.modelName = modelName;
        this.version = version;
        this.lastTrainingDate = lastTrainingDate;
        this.notes = notes;
        this.featuresUsed = featuresUsed;
    }

    public Long getId() { return id; }
    public String getModelName() { return modelName; }
    public String getVersion() { return version; }
    public String getLastTrainingDate() { return lastTrainingDate; }
    public String getNotes() { return notes; }
    public Map<String, Object> getFeaturesUsed() { return featuresUsed; }
}
 