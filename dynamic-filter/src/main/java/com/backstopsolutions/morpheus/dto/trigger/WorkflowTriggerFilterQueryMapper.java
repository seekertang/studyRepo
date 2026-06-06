package com.backstopsolutions.morpheus.dto.trigger;

import com.backstopsolutions.morpheus.dto.filter.FilterQueryDto;

public interface WorkflowTriggerFilterQueryMapper {
    FilterQueryDto map(WorkflowTriggerConfigDto config, WorkflowTriggerMappingContext context);
}