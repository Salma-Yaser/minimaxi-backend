package com.minimaxi.backend.dto.request;

import java.util.List;

public class CompleteWorkOrderRequest {
    private String actionTaken;
    private String rootCause;
    private List<SparePart> spareParts;
    private Integer hoursSpent;
    private Integer minutesSpent;
    private String additionalNotes;
    private Long completedByUserId;

    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }
    public String getRootCause() { return rootCause; }
    public void setRootCause(String rootCause) { this.rootCause = rootCause; }
    public List<SparePart> getSpareParts() { return spareParts; }
    public void setSpareParts(List<SparePart> spareParts) { this.spareParts = spareParts; }
    public Integer getHoursSpent() { return hoursSpent; }
    public void setHoursSpent(Integer hoursSpent) { this.hoursSpent = hoursSpent; }
    public Integer getMinutesSpent() { return minutesSpent; }
    public void setMinutesSpent(Integer minutesSpent) { this.minutesSpent = minutesSpent; }
    public String getAdditionalNotes() { return additionalNotes; }
    public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }
    public Long getCompletedByUserId() { return completedByUserId; }
    public void setCompletedByUserId(Long completedByUserId) { this.completedByUserId = completedByUserId; }

    public static class SparePart {
        private String name;
        private Integer quantity;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}