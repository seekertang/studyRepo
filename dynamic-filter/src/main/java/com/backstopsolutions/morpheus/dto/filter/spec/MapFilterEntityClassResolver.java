package com.backstopsolutions.morpheus.dto.filter.spec;

import java.util.Map;

public class MapFilterEntityClassResolver implements FilterEntityClassResolver {
    private final Map<String, Class<?>> mapping;

    public MapFilterEntityClassResolver(Map<String, Class<?>> mapping) {
        this.mapping = mapping;
    }

    @Override
    public Class<?> resolve(String entityName) {
        if (entityName == null) {
            return null;
        }
        return mapping.get(entityName);
    }
}
