package com.backstopsolutions.morpheus.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterQueryDto {
    private String entity;

    private List<String> select;

    private FilterNodeDto filter;

    private List<FilterJoinDto> joins;

    private List<FilterSortDto> sort;

    private FilterPageDto page;

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public List<String> getSelect() {
        return select;
    }

    public void setSelect(List<String> select) {
        this.select = select;
    }

    public FilterNodeDto getFilter() {
        return filter;
    }

    public void setFilter(FilterNodeDto filter) {
        this.filter = filter;
    }

    public List<FilterJoinDto> getJoins() {
        return joins;
    }

    public void setJoins(List<FilterJoinDto> joins) {
        this.joins = joins;
    }

    public List<FilterSortDto> getSort() {
        return sort;
    }

    public void setSort(List<FilterSortDto> sort) {
        this.sort = sort;
    }

    public FilterPageDto getPage() {
        return page;
    }

    public void setPage(FilterPageDto page) {
        this.page = page;
    }
}
