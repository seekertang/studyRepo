package com.ethansolutions.morpheus.dto.filter;

public class FilterSortDto {
    private String field;

    private FilterSortDirection direction = FilterSortDirection.ASC;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public FilterSortDirection getDirection() {
        return direction;
    }

    public void setDirection(FilterSortDirection direction) {
        this.direction = direction;
    }
}
