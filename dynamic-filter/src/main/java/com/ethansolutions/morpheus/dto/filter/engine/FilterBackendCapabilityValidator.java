package com.ethansolutions.morpheus.dto.filter.engine;

import com.ethansolutions.morpheus.dto.filter.FilterNodeDto;
import com.ethansolutions.morpheus.dto.filter.FilterNodeType;
import com.ethansolutions.morpheus.dto.filter.FilterQueryDto;
import com.ethansolutions.morpheus.dto.filter.FilterValidator;

import java.util.ArrayList;
import java.util.List;

public final class FilterBackendCapabilityValidator {

    private FilterBackendCapabilityValidator() {
    }

    public static void ensureSupported(FilterQueryDto query, FilterBackendType backendType) {
        FilterValidator.validate(query);

        List<String> violations = validate(query, backendType);
        if (!violations.isEmpty()) {
            throw new FilterCompilationException("Unsupported filter features for backend "
                    + backendType.name() + ": " + String.join("; ", violations));
        }
    }

    public static List<String> validate(FilterQueryDto query, FilterBackendType backendType) {
        List<String> violations = new ArrayList<>();
        if (query == null || query.getFilter() == null) {
            return violations;
        }

        if (backendType == FilterBackendType.JPA) {
            return violations;
        }

        if (query.getJoins() != null && !query.getJoins().isEmpty()) {
            violations.add("Top-level joins are not supported");
        }

        validateNode(query.getFilter(), backendType, violations);
        return violations;
    }

    private static void validateNode(FilterNodeDto node, FilterBackendType backendType, List<String> violations) {
        if (node == null || node.getType() == null) {
            return;
        }

        if (node.getType() == FilterNodeType.EXISTS) {
            if (backendType == FilterBackendType.MONGO) {
                violations.add("EXISTS is not supported in Mongo compiler skeleton");
            }
            if (backendType == FilterBackendType.OPENSEARCH) {
                violations.add("EXISTS is not supported in OpenSearch compiler skeleton");
            }
        }

        if (node.getJoins() != null && !node.getJoins().isEmpty()) {
            violations.add("EXISTS joins are not supported in " + backendType.name() + " compiler skeleton");
        }

        if (node.getChildren() != null) {
            for (FilterNodeDto child : node.getChildren()) {
                validateNode(child, backendType, violations);
            }
        }

        if (node.getSubFilter() != null) {
            validateNode(node.getSubFilter(), backendType, violations);
        }
    }
}
