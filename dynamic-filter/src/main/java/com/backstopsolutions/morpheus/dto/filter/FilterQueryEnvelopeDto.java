package com.backstopsolutions.morpheus.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterQueryEnvelopeDto {
    private FilterQueryDto filterQuery;

    public FilterQueryDto getFilterQuery() {
        return filterQuery;
    }

    public void setFilterQuery(FilterQueryDto filterQuery) {
        this.filterQuery = filterQuery;
    }
}
