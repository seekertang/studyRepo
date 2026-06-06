package com.ethansolutions.morpheus.dto.filter.engine;

import com.ethansolutions.morpheus.dto.filter.FilterQueryDto;

public interface FilterQueryCompiler<R> {
    FilterBackendType backendType();

    R compile(FilterQueryDto query, String rootEntityName);
}
