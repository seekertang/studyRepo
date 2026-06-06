package com.backstopsolutions.morpheus.dto.model;

public class WorkflowModelDesignDto {
    private String id;
    private String bpmnXml;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBpmnXml() {
        return bpmnXml;
    }

    public void setBpmnXml(String bpmnXml) {
        this.bpmnXml = bpmnXml;
    }
}
