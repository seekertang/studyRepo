package com.backstopsolutions.morpheus.dto.filter;

import com.backstopsolutions.morpheus.core.WorkflowRuntimeException;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

public final class FilterValidator {

    private FilterValidator() {
    }

    public static void validate(FilterQueryDto query) {
        if (query == null || query.getFilter() == null) {
            return;
        }
        validateJoins(query.getJoins(), "joins");
        validateNode(query.getFilter(), "filter");
    }

    private static void validateNode(FilterNodeDto node, String path) {
        WorkflowRuntimeException.check(node != null, "{0} is required", path);
        WorkflowRuntimeException.check(node.getType() != null, "{0}.type is required", path);

        if (node.getType() == FilterNodeType.GROUP) {
            validateGroup(node, path);
            return;
        }

        if (node.getType() == FilterNodeType.CONDITION) {
            validateCondition(node, path);
            return;
        }

        validateExists(node, path);
    }

    private static void validateGroup(FilterNodeDto node, String path) {
        WorkflowRuntimeException.check(node.getLogic() != null, "{0}.logic is required for GROUP", path);

        List<FilterNodeDto> children = node.getChildren();
        WorkflowRuntimeException.check(children != null && !children.isEmpty(), "{0}.children must not be empty for GROUP", path);

        if (node.getLogic() == FilterLogicType.NOT) {
            WorkflowRuntimeException.check(children.size() == 1, "{0}.children must contain exactly one node when logic is NOT", path);
        }

        for (int i = 0; i < children.size(); i++) {
            validateNode(children.get(i), path + ".children[" + i + "]");
        }
    }

    private static void validateCondition(FilterNodeDto node, String path) {
        WorkflowRuntimeException.check(hasText(node.getField()), "{0}.field is required for CONDITION", path);
        WorkflowRuntimeException.check(node.getOperator() != null, "{0}.operator is required for CONDITION", path);

        FilterOperator operator = node.getOperator();
        FilterValueDto value = node.getValue();

        if (!operator.requiresValue()) {
            WorkflowRuntimeException.check(value == null || value.getData() == null,
                    "{0}.value must be empty for operator {1}", path, operator.name());
            return;
        }

        WorkflowRuntimeException.check(value != null, "{0}.value is required for operator {1}", path, operator.name());

        Object data = value.getData();
        WorkflowRuntimeException.check(data != null, "{0}.value.data is required for operator {1}", path, operator.name());

        if (!operator.expectsArrayValue()) {
            return;
        }

        int size = getArrayLikeSize(data);
        WorkflowRuntimeException.check(size > 0, "{0}.value.data must be a non-empty array/collection for operator {1}", path, operator.name());

        if (operator.expectsTwoValues()) {
            WorkflowRuntimeException.check(size == 2, "{0}.value.data must contain exactly 2 values for operator BETWEEN", path);
        }
    }

    private static void validateExists(FilterNodeDto node, String path) {
        WorkflowRuntimeException.check(hasText(node.getSubEntity()), "{0}.subEntity is required for EXISTS", path);
        WorkflowRuntimeException.check(node.getCorrelation() != null, "{0}.correlation is required for EXISTS", path);
        WorkflowRuntimeException.check(node.getSubFilter() != null, "{0}.subFilter is required for EXISTS", path);

        FilterCorrelationDto correlation = node.getCorrelation();
        WorkflowRuntimeException.check(hasText(correlation.getParentField()), "{0}.correlation.parentField is required", path);
        WorkflowRuntimeException.check(hasText(correlation.getSubField()), "{0}.correlation.subField is required", path);

        validateJoins(node.getJoins(), path + ".joins");

        validateNode(node.getSubFilter(), path + ".subFilter");
    }

    private static void validateJoins(List<FilterJoinDto> joins, String path) {
        if (joins == null) {
            return;
        }

        for (int i = 0; i < joins.size(); i++) {
            FilterJoinDto join = joins.get(i);
            String joinPath = path + "[" + i + "]";
            WorkflowRuntimeException.check(join != null, "{0} is required", joinPath);
            WorkflowRuntimeException.check(hasText(join.getAlias()), "{0}.alias is required", joinPath);
            WorkflowRuntimeException.check(hasText(join.getTargetEntity()), "{0}.targetEntity is required", joinPath);
            WorkflowRuntimeException.check(hasText(join.getSourceField()), "{0}.sourceField is required", joinPath);
            WorkflowRuntimeException.check(hasText(join.getTargetField()), "{0}.targetField is required", joinPath);
            WorkflowRuntimeException.check(join.getJoinType() != null, "{0}.joinType is required", joinPath);
        }
    }

    private static int getArrayLikeSize(Object value) {
        if (value instanceof Collection<?> collection) {
            return collection.size();
        }

        if (value.getClass().isArray()) {
            return Array.getLength(value);
        }

        return -1;
    }

    private static boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
