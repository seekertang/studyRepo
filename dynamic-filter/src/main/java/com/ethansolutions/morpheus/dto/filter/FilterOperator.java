package com.ethansolutions.morpheus.dto.filter;

public enum FilterOperator {
    EQ,
    NE,
    GT,
    GTE,
    LT,
    LTE,
    IN,
    NOT_IN,
    BETWEEN,
    LIKE,
    STARTS_WITH,
    ENDS_WITH,
    IS_NULL,
    IS_NOT_NULL;

    public boolean requiresValue() {
        return this != IS_NULL && this != IS_NOT_NULL;
    }

    public boolean expectsArrayValue() {
        return this == IN || this == NOT_IN || this == BETWEEN;
    }

    public boolean expectsTwoValues() {
        return this == BETWEEN;
    }
}
