package com.backstopsolutions.morpheus.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkflowProcessStatus {
    RUNNING("running"),
    TERMINATED("terminated"),
    COMPLETED("completed"),
    CANCELED("canceled");

    private final String code;
}
