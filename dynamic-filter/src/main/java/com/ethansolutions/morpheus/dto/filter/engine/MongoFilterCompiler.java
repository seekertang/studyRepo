package com.ethansolutions.morpheus.dto.filter.engine;

import com.ethansolutions.morpheus.dto.filter.FilterQueryDto;

import java.util.Map;

public class MongoFilterCompiler implements FilterQueryCompiler<Map<String, Object>> {
    @Override
    public FilterBackendType backendType() {
        return FilterBackendType.MONGO;
    }

    @Override
    public Map<String, Object> compile(FilterQueryDto query, String rootEntityName) {
        FilterBackendCapabilityValidator.ensureSupported(query, FilterBackendType.MONGO);
        throw new FilterCompilationException("Mongo compiler skeleton is not implemented yet");
    }
}
