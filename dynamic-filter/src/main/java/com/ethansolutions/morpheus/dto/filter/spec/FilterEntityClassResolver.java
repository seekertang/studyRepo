package com.ethansolutions.morpheus.dto.filter.spec;

public interface FilterEntityClassResolver {
    Class<?> resolve(String entityName);
}
