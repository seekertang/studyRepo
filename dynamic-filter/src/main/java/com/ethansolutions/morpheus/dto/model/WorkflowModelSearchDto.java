package com.ethansolutions.morpheus.dto.model;

import com.ethansolutions.morpheus.dto.PageDto;
import com.ethansolutions.morpheus.dto.filter.FilterQueryDto;

public class WorkflowModelSearchDto extends PageDto {
    private String name;
    private String key;
    private FilterQueryDto filterQuery;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public FilterQueryDto getFilterQuery() {
        return filterQuery;
    }

    public void setFilterQuery(FilterQueryDto filterQuery) {
        this.filterQuery = filterQuery;
    }
}
