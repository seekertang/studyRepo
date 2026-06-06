package com.backstopsolutions.morpheus.dto.filter.spec;

import jakarta.persistence.criteria.From;

import java.util.HashMap;
import java.util.Map;

final class FilterCompileContext {

    private final From<?, ?> baseFrom;
    private final Map<String, From<?, ?>> aliases;

    private FilterCompileContext(From<?, ?> baseFrom, Map<String, From<?, ?>> aliases) {
        this.baseFrom = baseFrom;
        this.aliases = aliases;
    }

    static FilterCompileContext of(From<?, ?> baseFrom) {
        return new FilterCompileContext(baseFrom, new HashMap<>());
    }

    From<?, ?> getBaseFrom() {
        return baseFrom;
    }

    From<?, ?> getAlias(String alias) {
        return aliases.get(alias);
    }

    void putAlias(String alias, From<?, ?> from) {
        aliases.put(alias, from);
    }
}