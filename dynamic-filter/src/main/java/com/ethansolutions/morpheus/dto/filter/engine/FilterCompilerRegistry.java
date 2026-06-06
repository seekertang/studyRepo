package com.ethansolutions.morpheus.dto.filter.engine;

import java.util.HashMap;
import java.util.Map;

public class FilterCompilerRegistry {
    private final Map<FilterBackendType, FilterQueryCompiler<?>> compilers = new HashMap<>();

    public void register(FilterQueryCompiler<?> compiler) {
        compilers.put(compiler.backendType(), compiler);
    }

    @SuppressWarnings("unchecked")
    public <R> FilterQueryCompiler<R> get(FilterBackendType backendType) {
        FilterQueryCompiler<?> compiler = compilers.get(backendType);
        if (compiler == null) {
            throw new FilterCompilationException("No compiler registered for backend " + backendType.name());
        }
        return (FilterQueryCompiler<R>) compiler;
    }
}
