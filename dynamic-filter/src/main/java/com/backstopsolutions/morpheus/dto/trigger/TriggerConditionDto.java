package com.backstopsolutions.morpheus.dto.trigger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TriggerConditionDto {
    private TriggerBooleanOperator joinWithPrevious;

    private String fieldKey;

    private String fieldLabel;

    private TriggerFieldOperator operator;

    private Object value;

    public TriggerBooleanOperator getJoinWithPrevious() {
        return joinWithPrevious;
    }

    public void setJoinWithPrevious(TriggerBooleanOperator joinWithPrevious) {
        this.joinWithPrevious = joinWithPrevious;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public TriggerFieldOperator getOperator() {
        return operator;
    }

    public void setOperator(TriggerFieldOperator operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}