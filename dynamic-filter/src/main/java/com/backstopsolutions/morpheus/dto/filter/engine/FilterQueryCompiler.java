package com.backstopsolutions.morpheus.dto.filter.engine;

import com.backstopsolutions.morpheus.dto.filter.FilterQueryDto;

public interface FilterQueryCompiler<R> {
    FilterBackendType backendType();

    R compile(FilterQueryDto query, String rootEntityName);
}
