package com.backstopsolutions.morpheus.core;

import java.text.MessageFormat;

public class WorkflowRuntimeException extends RuntimeException {
    private Integer code;

    public WorkflowRuntimeException(String message) {
        super(message);
        this.code = CodeEnum.BUSINESS_INVALID.getCode();
    }

    public WorkflowRuntimeException(String message, Object ... params) {
        this(MessageFormat.format(message, params));
    }

    public WorkflowRuntimeException(CodeEnum error) {
        super(error.getMsg());
        this.code = error.getCode();
    }

    public WorkflowRuntimeException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static void check(boolean condition, CodeEnum error) {
        if (!condition) {
            throw new WorkflowRuntimeException(error);
        }
    }

    public static void check(boolean condition, String message, Object... params) {
        if (!condition) {
            if (params == null) {
                throw new WorkflowRuntimeException(message);
            }
            throw new WorkflowRuntimeException(MessageFormat.format(message, params));
        }
    }
}
