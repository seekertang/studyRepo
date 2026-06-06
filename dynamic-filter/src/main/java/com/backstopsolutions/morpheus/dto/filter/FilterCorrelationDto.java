package com.backstopsolutions.morpheus.dto.filter;

public class FilterCorrelationDto {
    private String parentField;

    private String subField;

    public String getParentField() {
        return parentField;
    }

    public void setParentField(String parentField) {
        this.parentField = parentField;
    }

    public String getSubField() {
        return subField;
    }

    public void setSubField(String subField) {
        this.subField = subField;
    }
}
