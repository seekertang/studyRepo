package com.backstopsolutions.morpheus.dto.filter.engine;

import com.backstopsolutions.morpheus.dto.filter.FilterQueryDto;
import com.backstopsolutions.morpheus.dto.filter.spec.FilterEntityClassResolver;
import org.springframework.data.jpa.domain.Specification;

public final class FilterEngineUsageExample {

    private FilterEngineUsageExample() {
    }

    public static <T> Specification<T> compileJpa(FilterQueryDto query,
                                                  Class<T> rootEntityClass,
                                                  FilterEntityClassResolver resolver) {
        FilterCompilerRegistry registry = new FilterCompilerRegistry();
        registry.register(new JpaFilterSpecificationCompiler<>(rootEntityClass, resolver));
        registry.register(new MongoFilterCompiler());
        registry.register(new OpenSearchFilterCompiler());

        return registry.<Specification<T>>get(FilterBackendType.JPA).compile(query, rootEntityClass.getSimpleName());
    }
}
