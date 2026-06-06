package com.ethansolutions.morpheus.dto.trigger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowTriggerTargetDto {
    private String primaryEntityType;

    private String activityType;

    public String getPrimaryEntityType() {
        return primaryEntityType;
    }

    public void setPrimaryEntityType(String primaryEntityType) {
        this.primaryEntityType = primaryEntityType;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
}