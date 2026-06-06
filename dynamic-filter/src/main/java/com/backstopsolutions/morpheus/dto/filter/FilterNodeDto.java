package com.backstopsolutions.morpheus.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterNodeDto {
    private FilterNodeType type;

    private FilterLogicType logic;

    private List<FilterNodeDto> children;

    private String field;

    private FilterOperator operator;

    private FilterValueDto value;

    private String subEntity;

    private Boolean not = false;

    private FilterCorrelationDto correlation;

    private List<FilterJoinDto> joins;

    private FilterNodeDto subFilter;

    public FilterNodeType getType() {
        return type;
    }

    public void setType(FilterNodeType type) {
        this.type = type;
    }

    public FilterLogicType getLogic() {
        return logic;
    }

    public void setLogic(FilterLogicType logic) {
        this.logic = logic;
    }

    public List<FilterNodeDto> getChildren() {
        return children;
    }

    public void setChildren(List<FilterNodeDto> children) {
        this.children = children;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public FilterOperator getOperator() {
        return operator;
    }

    public void setOperator(FilterOperator operator) {
        this.operator = operator;
    }

    public FilterValueDto getValue() {
        return value;
    }

    public void setValue(FilterValueDto value) {
        this.value = value;
    }

    public String getSubEntity() {
        return subEntity;
    }

    public void setSubEntity(String subEntity) {
        this.subEntity = subEntity;
    }

    public Boolean getNot() {
        return not;
    }

    public void setNot(Boolean not) {
        this.not = not;
    }

    public FilterCorrelationDto getCorrelation() {
        return correlation;
    }

    public void setCorrelation(FilterCorrelationDto correlation) {
        this.correlation = correlation;
    }

    public List<FilterJoinDto> getJoins() {
        return joins;
    }

    public void setJoins(List<FilterJoinDto> joins) {
        this.joins = joins;
    }

    public FilterNodeDto getSubFilter() {
        return subFilter;
    }

    public void setSubFilter(FilterNodeDto subFilter) {
        this.subFilter = subFilter;
    }
}
