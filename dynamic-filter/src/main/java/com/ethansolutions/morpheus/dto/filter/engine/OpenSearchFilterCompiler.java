package com.ethansolutions.morpheus.dto.filter.engine;

import com.ethansolutions.morpheus.dto.filter.FilterQueryDto;

import java.util.Map;

public class OpenSearchFilterCompiler implements FilterQueryCompiler<Map<String, Object>> {
    @Override
    public FilterBackendType backendType() {
        return FilterBackendType.OPENSEARCH;
    }

    @Override
    public Map<String, Object> compile(FilterQueryDto query, String rootEntityName) {
        FilterBackendCapabilityValidator.ensureSupported(query, FilterBackendType.OPENSEARCH);
        throw new FilterCompilationException("OpenSearch compiler skeleton is not implemented yet");
    }
}
