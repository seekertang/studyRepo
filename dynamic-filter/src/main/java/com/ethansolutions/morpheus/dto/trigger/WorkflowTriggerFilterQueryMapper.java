package com.ethansolutions.morpheus.dto.trigger;

import com.ethansolutions.morpheus.dto.filter.FilterQueryDto;

public interface WorkflowTriggerFilterQueryMapper {
    FilterQueryDto map(WorkflowTriggerConfigDto config, WorkflowTriggerMappingContext context);
}