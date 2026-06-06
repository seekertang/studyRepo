package com.backstopsolutions.morpheus.dto;

import java.util.ArrayList;
import java.util.List;

public class PageContainer<T> {
    private int pageIndex = 1;
    private int pageSize = 10;

    private List<T> result = new ArrayList<>();

    private int totalCount = 0;

    public PageContainer(PageDto pageDto, long totalCount) {
        this.pageIndex = pageDto.getPageIndex();
        this.pageSize = pageDto.getPageSize();
        this.totalCount = (int) totalCount;
    }

    public PageContainer(List<T> result, PageDto pageDto, long totalCount) {
        this(pageDto, totalCount);
        this.result = result;
    }

    public PageContainer(List<T> result, long current, long pageSize, long totalCount) {
        this.pageIndex = (int) current;
        this.pageSize = (int) pageSize;
        this.result = result;
        this.totalCount = (int) totalCount;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public int getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
