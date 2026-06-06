package com.backstopsolutions.morpheus.dto.filter.spec;

import com.backstopsolutions.morpheus.dto.filter.FilterQueryDto;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

public final class FilterSpecificationUsageExample {

    private FilterSpecificationUsageExample() {
    }

    public static <T> Specification<T> buildSpecification(FilterQueryDto query, Class<T> rootClass,
                                                          Map<String, Class<?>> subEntityMapping) {
        FilterEntityClassResolver resolver = new MapFilterEntityClassResolver(subEntityMapping);
        return FilterSpecificationBuilder.build(query, rootClass, resolver);
    }
}
