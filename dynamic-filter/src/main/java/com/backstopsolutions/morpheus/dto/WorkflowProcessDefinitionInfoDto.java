package com.backstopsolutions.morpheus.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class WorkflowProcessDefinitionInfoDto {
    private String id;
    private String name;
    private String key;
    private String category;
    private String version;
    private Date deploymentTime;
    private Boolean active;
}
