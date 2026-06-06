package com.ethansolutions.morpheus.dto.trigger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RelativeDateValueDto {
    private Integer amount;

    private RelativeDateUnit unit;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public RelativeDateUnit getUnit() {
        return unit;
    }

    public void setUnit(RelativeDateUnit unit) {
        this.unit = unit;
    }
}