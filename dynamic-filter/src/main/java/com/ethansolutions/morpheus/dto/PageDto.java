package com.ethansolutions.morpheus.dto;

public class PageDto {
    private int pageIndex = 1;

    private int pageSize = Integer.MAX_VALUE;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getOffset() {
        return (pageIndex - 1) * pageSize;
    }
}
