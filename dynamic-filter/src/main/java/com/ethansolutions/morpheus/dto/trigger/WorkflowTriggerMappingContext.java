package com.ethansolutions.morpheus.dto.trigger;

import com.ethansolutions.morpheus.core.WorkflowRuntimeException;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

public class WorkflowTriggerMappingContext {
    private String rootEntity;

    private String activityEntity;

    private String correlationParentField;

    private String correlationSubField;

    private String activityTypeField;

    private Map<String, String> fieldPathByKey = new HashMap<>();

    private Clock clock = Clock.systemUTC();

    public String getRootEntity() {
        return rootEntity;
    }

    public void setRootEntity(String rootEntity) {
        this.rootEntity = rootEntity;
    }

    public String getActivityEntity() {
        return activityEntity;
    }

    public void setActivityEntity(String activityEntity) {
        this.activityEntity = activityEntity;
    }

    public String getCorrelationParentField() {
        return correlationParentField;
    }

    public void setCorrelationParentField(String correlationParentField) {
        this.correlationParentField = correlationParentField;
    }

    public String getCorrelationSubField() {
        return correlationSubField;
    }

    public void setCorrelationSubField(String correlationSubField) {
        this.correlationSubField = correlationSubField;
    }

    public String getActivityTypeField() {
        return activityTypeField;
    }

    public void setActivityTypeField(String activityTypeField) {
        this.activityTypeField = activityTypeField;
    }

    public Map<String, String> getFieldPathByKey() {
        return fieldPathByKey;
    }

    public void setFieldPathByKey(Map<String, String> fieldPathByKey) {
        this.fieldPathByKey = fieldPathByKey;
    }

    public Clock getClock() {
        return clock;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public String requireFieldPath(String fieldKey) {
        WorkflowRuntimeException.check(fieldPathByKey != null && fieldPathByKey.containsKey(fieldKey),
            "Unsupported workflow trigger fieldKey [{0}]",
            fieldKey);
        return fieldPathByKey.get(fieldKey);
    }
}