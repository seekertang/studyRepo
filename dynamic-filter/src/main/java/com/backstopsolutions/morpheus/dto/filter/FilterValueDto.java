package com.backstopsolutions.morpheus.dto.filter;

public class FilterValueDto {
    private FilterValueKind kind = FilterValueKind.LITERAL;

    private Object data;

    public FilterValueKind getKind() {
        return kind;
    }

    public void setKind(FilterValueKind kind) {
        this.kind = kind;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
