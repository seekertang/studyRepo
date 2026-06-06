package com.ethansolutions.morpheus.dto.trigger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TriggerConditionTreeNodeDto {
    private TriggerConditionNodeType type;

    private TriggerBooleanOperator logic;

    private List<TriggerConditionTreeNodeDto> children;

    private String fieldKey;

    private String fieldLabel;

    private TriggerFieldOperator operator;

    private Object value;

    public TriggerConditionNodeType getType() {
        return type;
    }

    public void setType(TriggerConditionNodeType type) {
        this.type = type;
    }

    public TriggerBooleanOperator getLogic() {
        return logic;
    }

    public void setLogic(TriggerBooleanOperator logic) {
        this.logic = logic;
    }

    public List<TriggerConditionTreeNodeDto> getChildren() {
        return children;
    }

    public void setChildren(List<TriggerConditionTreeNodeDto> children) {
        this.children = children;
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