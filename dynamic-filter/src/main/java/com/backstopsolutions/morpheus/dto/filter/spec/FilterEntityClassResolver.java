package com.backstopsolutions.morpheus.dto.filter.spec;

public interface FilterEntityClassResolver {
    Class<?> resolve(String entityName);
}
