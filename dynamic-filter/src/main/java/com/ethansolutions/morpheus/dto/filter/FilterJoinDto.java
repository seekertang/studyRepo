package com.ethansolutions.morpheus.dto.filter;

public class FilterJoinDto {
    private String alias;

    private String targetEntity;

    private String sourceField;

    private String targetField;

    private FilterJoinType joinType = FilterJoinType.INNER;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    public String getSourceField() {
        return sourceField;
    }

    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }

    public String getTargetField() {
        return targetField;
    }

    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }

    public FilterJoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(FilterJoinType joinType) {
        this.joinType = joinType;
    }
}
