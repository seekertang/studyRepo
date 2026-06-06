package com.backstopsolutions.morpheus.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class WorkflowProcessDefinitionStartDto {
    private String formKey;
    private String definitionId;
    private Map<String, Object> variables = new HashMap<>();
}
