package com.ethansolutions.morpheus.dto.filter.spec;

import com.ethansolutions.morpheus.core.WorkflowRuntimeException;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;

final class FilterPathResolver {

    Path<?> resolvePath(FilterCompileContext context, String fieldPath) {
        String[] segments = fieldPath.split("\\.");

        WorkflowRuntimeException.check(segments.length > 0,
            "Invalid field path [{0}]", fieldPath);

        From<?, ?> current = context.getBaseFrom();
        int startIndex = 0;

        From<?, ?> aliasFrom = context.getAlias(segments[0]);
        if (aliasFrom != null) {
            current = aliasFrom;
            startIndex = 1;
            WorkflowRuntimeException.check(segments.length > 1,
                "Invalid alias-only field path [{0}]", fieldPath);
        }

        for (int i = startIndex; i < segments.length - 1; i++) {
            current = current.join(segments[i]);
        }

        return current.get(segments[segments.length - 1]);
    }
}