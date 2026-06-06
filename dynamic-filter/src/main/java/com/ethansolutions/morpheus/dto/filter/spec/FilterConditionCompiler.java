package com.ethansolutions.morpheus.dto.filter.spec;

import com.ethansolutions.morpheus.core.WorkflowRuntimeException;
import com.ethansolutions.morpheus.dto.filter.FilterNodeDto;
import com.ethansolutions.morpheus.dto.filter.FilterOperator;
import com.ethansolutions.morpheus.dto.filter.FilterValueDto;
import com.ethansolutions.morpheus.dto.filter.FilterValueKind;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class FilterConditionCompiler {

    private final FilterPathResolver pathResolver;

    FilterConditionCompiler(FilterPathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    Specification<Object> buildConditionSpecification(FilterNodeDto node, FilterCompileContext context) {
        Path<?> path = pathResolver.resolvePath(context, node.getField());
        FilterOperator operator = node.getOperator();
        FilterValueDto value = node.getValue();
        Object data = value == null ? null : value.getData();
        boolean isFieldRef = value != null && value.getKind() == FilterValueKind.FIELD_REF && data instanceof String;

        if (operator == FilterOperator.IS_NULL) {
            return (root, query, cb) -> cb.isNull(path);
        }

        if (operator == FilterOperator.IS_NOT_NULL) {
            return (root, query, cb) -> cb.isNotNull(path);
        }

        Expression<?> rightExpr = isFieldRef
            ? pathResolver.resolvePath(context, (String) data)
            : null;

        return switch (operator) {
            case EQ -> (root, query, cb) -> rightExpr == null ? cb.equal(path, data) : cb.equal(path, rightExpr);
            case NE -> (root, query, cb) -> rightExpr == null ? cb.notEqual(path, data) : cb.notEqual(path, rightExpr);
            case GT -> (root, query, cb) -> compileGreaterThan(path, rightExpr, data, cb);
            case GTE -> (root, query, cb) -> compileGreaterThanOrEqual(path, rightExpr, data, cb);
            case LT -> (root, query, cb) -> compileLessThan(path, rightExpr, data, cb);
            case LTE -> (root, query, cb) -> compileLessThanOrEqual(path, rightExpr, data, cb);
            case IN -> (root, query, cb) -> compileIn(path, data, cb, false);
            case NOT_IN -> (root, query, cb) -> compileIn(path, data, cb, true);
            case BETWEEN -> (root, query, cb) -> compileBetween(path, data, cb);
            case LIKE -> (root, query, cb) -> cb.like(path.as(String.class), String.valueOf(data));
            case STARTS_WITH -> (root, query, cb) -> cb.like(path.as(String.class), String.valueOf(data) + "%");
            case ENDS_WITH -> (root, query, cb) -> cb.like(path.as(String.class), "%" + data);
            case IS_NULL, IS_NOT_NULL -> (root, query, cb) -> cb.conjunction();
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate compileGreaterThan(Path<?> left, Expression<?> rightExpr, Object data, CriteriaBuilder cb) {
        if (rightExpr != null) {
            return cb.greaterThan((Expression<? extends Comparable>) left, (Expression<? extends Comparable>) rightExpr);
        }
        return cb.greaterThan((Expression<? extends Comparable>) left, (Comparable) coerceValue(left, data));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate compileGreaterThanOrEqual(Path<?> left, Expression<?> rightExpr, Object data, CriteriaBuilder cb) {
        if (rightExpr != null) {
            return cb.greaterThanOrEqualTo((Expression<? extends Comparable>) left, (Expression<? extends Comparable>) rightExpr);
        }
        return cb.greaterThanOrEqualTo((Expression<? extends Comparable>) left, (Comparable) coerceValue(left, data));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate compileLessThan(Path<?> left, Expression<?> rightExpr, Object data, CriteriaBuilder cb) {
        if (rightExpr != null) {
            return cb.lessThan((Expression<? extends Comparable>) left, (Expression<? extends Comparable>) rightExpr);
        }
        return cb.lessThan((Expression<? extends Comparable>) left, (Comparable) coerceValue(left, data));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate compileLessThanOrEqual(Path<?> left, Expression<?> rightExpr, Object data, CriteriaBuilder cb) {
        if (rightExpr != null) {
            return cb.lessThanOrEqualTo((Expression<? extends Comparable>) left, (Expression<? extends Comparable>) rightExpr);
        }
        return cb.lessThanOrEqualTo((Expression<? extends Comparable>) left, (Comparable) coerceValue(left, data));
    }

    private Object coerceValue(Path<?> path, Object data) {
        if (data == null) {
            return null;
        }
        Class<?> javaType = path.getJavaType();
        if (javaType == null || javaType.isInstance(data)) {
            return data;
        }
        String str = data.toString();
        if (LocalDateTime.class.equals(javaType)) {
            try {
                return LocalDateTime.parse(str);
            } catch (DateTimeParseException e) {
                return LocalDate.parse(str).atStartOfDay();
            }
        }
        if (LocalDate.class.equals(javaType)) {
            return LocalDate.parse(str);
        }
        if (LocalTime.class.equals(javaType)) {
            return LocalTime.parse(str);
        }
        return data;
    }

    private Predicate compileIn(Path<?> path, Object data, CriteriaBuilder cb, boolean negated) {
        List<Object> values = toList(data);
        CriteriaBuilder.In<Object> in = cb.in(path);
        for (Object value : values) {
            in.value(value);
        }
        return negated ? cb.not(in) : in;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate compileBetween(Path<?> path, Object data, CriteriaBuilder cb) {
        List<Object> values = toList(data);
        Comparable start = (Comparable) coerceValue(path, values.get(0));
        Comparable end = (Comparable) coerceValue(path, values.get(1));
        return cb.between((Expression<? extends Comparable>) path, start, end);
    }

    private List<Object> toList(Object data) {
        if (data instanceof Collection<?> collection) {
            return new ArrayList<>(collection);
        }

        if (data != null && data.getClass().isArray()) {
            int len = Array.getLength(data);
            List<Object> values = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                values.add(Array.get(data, i));
            }
            return values;
        }

        throw new WorkflowRuntimeException("Expected array/collection value for operator");
    }
}