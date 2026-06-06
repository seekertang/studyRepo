package com.backstopsolutions.morpheus.core;

public enum CodeEnum {
    SUCCESS(200, "success"),
    BUSINESS_INVALID(3000, "Business invalid");

    private final Integer code;
    private final String msg;

    CodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
