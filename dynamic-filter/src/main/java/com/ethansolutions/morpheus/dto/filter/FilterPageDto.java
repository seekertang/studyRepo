package com.ethansolutions.morpheus.dto.filter;

public class FilterPageDto {
    private int index = 1;

    private int size = Integer.MAX_VALUE;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
