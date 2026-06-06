package com.ethansolutions.morpheus.dto.filter.spec;

import com.ethansolutions.morpheus.core.WorkflowRuntimeException;
import com.ethansolutions.morpheus.dto.filter.FilterLogicType;
import com.ethansolutions.morpheus.dto.filter.FilterNodeDto;
import jakarta.persistence.criteria.CommonAbstractCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

final class FilterNodeCompiler {

    private final FilterPathResolver pathResolver;
    private final FilterJoinCompiler joinCompiler;
    private final FilterConditionCompiler conditionCompiler;
    private final FilterEntityClassResolver entityClassResolver;

    FilterNodeCompiler(FilterPathResolver pathResolver,
                       FilterJoinCompiler joinCompiler,
                       FilterConditionCompiler conditionCompiler,
                       FilterEntityClassResolver entityClassResolver) {
        this.pathResolver = pathResolver;
        this.joinCompiler = joinCompiler;
        this.conditionCompiler = conditionCompiler;
        this.entityClassResolver = entityClassResolver;
    }

    Predicate compileNode(FilterNodeDto node, From<?, ?> rootFrom, FilterCompileContext context,
                          CommonAbstractCriteria currentQuery, CriteriaBuilder cb) {
        if (node.getType() == null) {
            throw new WorkflowRuntimeException("filter node type is required");
        }

        return switch (node.getType()) {
            case GROUP -> compileGroup(node, rootFrom, context, currentQuery, cb);
            case CONDITION -> compileCondition(node, rootFrom, context, currentQuery, cb);
            case EXISTS -> compileExists(node, rootFrom, currentQuery, cb);
        };
    }

    static <T> Specification<T> toAndSpecification(List<Predicate> predicates) {
        if (predicates == null || predicates.isEmpty()) {
            return null;
        }

        return (root, query, cb) -> cb.and(predicates.toArray(Predicate[]::new));
    }

    private Predicate compileGroup(FilterNodeDto node, From<?, ?> rootFrom, FilterCompileContext context,
                                   CommonAbstractCriteria currentQuery, CriteriaBuilder cb) {
        if (currentQuery instanceof CriteriaQuery<?> criteriaQuery && rootFrom instanceof Root<?> root) {
            @SuppressWarnings("unchecked")
            Root<Object> typedRoot = (Root<Object>) root;

            List<Specification<Object>> childSpecifications = new ArrayList<>();
            for (FilterNodeDto child : node.getChildren()) {
                childSpecifications.add((ignoredRoot, ignoredQuery, builder) ->
                    compileNode(child, rootFrom, context, currentQuery, builder));
            }

            Specification<Object> combined = combineByLogic(node.getLogic(), childSpecifications);
            return combined.toPredicate(typedRoot, criteriaQuery, cb);
        }

        List<Predicate> children = new ArrayList<>();
        for (FilterNodeDto child : node.getChildren()) {
            children.add(compileNode(child, rootFrom, context, currentQuery, cb));
        }

        if (node.getLogic() == FilterLogicType.OR) {
            return cb.or(children.toArray(Predicate[]::new));
        }

        if (node.getLogic() == FilterLogicType.NOT) {
            return cb.not(children.get(0));
        }

        return cb.and(children.toArray(Predicate[]::new));
    }

    private Predicate compileCondition(FilterNodeDto node, From<?, ?> rootFrom, FilterCompileContext context,
                                       CommonAbstractCriteria currentQuery, CriteriaBuilder cb) {
        Specification<Object> conditionSpecification = conditionCompiler.buildConditionSpecification(node, context);

        if (currentQuery instanceof CriteriaQuery<?> criteriaQuery && rootFrom instanceof Root<?> root) {
            @SuppressWarnings("unchecked")
            Root<Object> typedRoot = (Root<Object>) root;
            return conditionSpecification.toPredicate(typedRoot, criteriaQuery, cb);
        }

        return conditionSpecification.toPredicate(null, null, cb);
    }

    private Predicate compileExists(FilterNodeDto node, From<?, ?> rootFrom,
                                    CommonAbstractCriteria currentQuery, CriteriaBuilder cb) {
        WorkflowRuntimeException.check(entityClassResolver != null,
            "FilterEntityClassResolver is required for EXISTS node");

        Class<?> subEntityClass = entityClassResolver.resolve(node.getSubEntity());
        WorkflowRuntimeException.check(subEntityClass != null,
            "No mapped entity class for subEntity [{0}]", node.getSubEntity());

        Subquery<Long> subquery = currentQuery.subquery(Long.class);
        Root<?> subRoot = subquery.from(subEntityClass);
        FilterCompileContext subContext = FilterCompileContext.of(subRoot);

        List<Predicate> manualJoinPredicates = joinCompiler.compileSubqueryJoins(node, subquery, subContext, cb);

        Path<?> parentPath = pathResolver.resolvePath(FilterCompileContext.of(rootFrom), node.getCorrelation().getParentField());
        Path<?> subPath = pathResolver.resolvePath(subContext, node.getCorrelation().getSubField());

        List<Specification<Object>> subSpecifications = new ArrayList<>();
        subSpecifications.add((ignoredRoot, ignoredQuery, builder) -> builder.equal(parentPath, subPath));
        Specification<Object> manualJoinSpecification = toAndSpecification(manualJoinPredicates);
        if (manualJoinSpecification != null) {
            subSpecifications.add(manualJoinSpecification);
        }
        subSpecifications.add((ignoredRoot, ignoredQuery, builder) ->
            compileNode(node.getSubFilter(), rootFrom, subContext, subquery, builder));

        Predicate subqueryPredicate = combineAndSpecifications(subSpecifications)
            .toPredicate(null, null, cb);

        subquery.select(cb.literal(1L)).where(subqueryPredicate);

        Predicate exists = cb.exists(subquery);
        return Boolean.TRUE.equals(node.getNot()) ? cb.not(exists) : exists;
    }

    private <T> Specification<T> combineByLogic(FilterLogicType logic,
                                                List<Specification<T>> childSpecifications) {
        if (logic == FilterLogicType.NOT) {
            Specification<T> first = childSpecifications.get(0);
            return (root, query, builder) -> builder.not(first.toPredicate(root, query, builder));
        }

        Specification<T> combined = null;
        for (Specification<T> child : childSpecifications) {
            combined = combined == null
                ? Specification.where(child)
                : (logic == FilterLogicType.OR ? combined.or(child) : combined.and(child));
        }

        return combined == null ? (root, query, builder) -> builder.conjunction() : combined;
    }

    private <T> Specification<T> combineAndSpecifications(List<Specification<T>> specifications) {
        Specification<T> combined = null;
        for (Specification<T> specification : specifications) {
            combined = combined == null
                ? Specification.where(specification)
                : combined.and(specification);
        }
        return combined == null ? (root, query, cb) -> cb.conjunction() : combined;
    }
}