package com.backstopsolutions.morpheus.dto.filter.engine;

import com.backstopsolutions.morpheus.dto.filter.FilterQueryDto;
import com.backstopsolutions.morpheus.dto.filter.spec.FilterEntityClassResolver;
import com.backstopsolutions.morpheus.dto.filter.spec.FilterSpecificationBuilder;
import org.springframework.data.jpa.domain.Specification;

public class JpaFilterSpecificationCompiler<T> implements FilterQueryCompiler<Specification<T>> {

    private final Class<T> rootEntityClass;
    private final FilterEntityClassResolver entityClassResolver;

    public JpaFilterSpecificationCompiler(Class<T> rootEntityClass, FilterEntityClassResolver entityClassResolver) {
        this.rootEntityClass = rootEntityClass;
        this.entityClassResolver = entityClassResolver;
    }

    @Override
    public FilterBackendType backendType() {
        return FilterBackendType.JPA;
    }

    @Override
    public Specification<T> compile(FilterQueryDto query, String rootEntityName) {
        FilterBackendCapabilityValidator.ensureSupported(query, FilterBackendType.JPA);
        return FilterSpecificationBuilder.build(query, rootEntityClass, entityClassResolver);
    }
}
