package com.ethansolutions.morpheus.dto.filter.spec;

import com.ethansolutions.morpheus.core.WorkflowRuntimeException;
import com.ethansolutions.morpheus.dto.filter.FilterQueryDto;
import com.ethansolutions.morpheus.dto.filter.FilterSortDirection;
import com.ethansolutions.morpheus.dto.filter.FilterSortDto;
import com.ethansolutions.morpheus.dto.filter.FilterValidator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class FilterSpecificationBuilder {

    private FilterSpecificationBuilder() {
    }

    public static <T> Specification<T> build(FilterQueryDto query, Class<T> rootEntityClass,
                                             FilterEntityClassResolver entityClassResolver) {
        FilterPathResolver pathResolver = new FilterPathResolver();
        FilterJoinCompiler joinCompiler = new FilterJoinCompiler(pathResolver, entityClassResolver);
        FilterConditionCompiler conditionCompiler = new FilterConditionCompiler(pathResolver);
        FilterNodeCompiler nodeCompiler = new FilterNodeCompiler(pathResolver, joinCompiler, conditionCompiler, entityClassResolver);

        return (root, criteriaQuery, cb) -> {
            if (query == null || query.getFilter() == null) {
                return cb.conjunction();
            }

            FilterValidator.validate(query);
            validateRootEntity(query, rootEntityClass);
            FilterCompileContext context = FilterCompileContext.of(root);
            List<Predicate> rootJoinPredicates = joinCompiler.compileRootJoins(query, criteriaQuery, context, cb);
            applySort(query, criteriaQuery, cb, context, pathResolver);

            Specification<T> rootJoinSpecification = FilterNodeCompiler.toAndSpecification(rootJoinPredicates);
            Specification<T> filterSpecification =
                (ignoredRoot, ignoredQuery, builder) ->
                    nodeCompiler.compileNode(query.getFilter(), root, context, criteriaQuery, builder);

            return Specification.where(rootJoinSpecification)
                .and(filterSpecification)
                .toPredicate(root, criteriaQuery, cb);
        };
    }

    private static <T> void validateRootEntity(FilterQueryDto query, Class<T> rootEntityClass) {
        if (query.getEntity() == null || query.getEntity().trim().isEmpty()) {
            return;
        }

        String configured = query.getEntity().trim();
        String actual = rootEntityClass.getSimpleName();
        WorkflowRuntimeException.check(configured.equals(actual),
                "filterQuery.entity [{0}] does not match root entity [{1}]", configured, actual);
    }

    private static void applySort(FilterQueryDto query, CriteriaQuery<?> criteriaQuery,
                                  CriteriaBuilder cb, FilterCompileContext context,
                                  FilterPathResolver pathResolver) {
        if (query.getSort() == null || query.getSort().isEmpty()) {
            return;
        }

        if (Long.class.equals(criteriaQuery.getResultType()) || long.class.equals(criteriaQuery.getResultType())) {
            return;
        }

        List<Order> orders = new ArrayList<>();
        for (FilterSortDto sort : query.getSort()) {
            if (sort == null || sort.getField() == null || sort.getField().trim().isEmpty()) {
                continue;
            }
            Path<?> path = pathResolver.resolvePath(context, sort.getField());
            if (sort.getDirection() == FilterSortDirection.DESC) {
                orders.add(cb.desc(path));
            } else {
                orders.add(cb.asc(path));
            }
        }

        if (!orders.isEmpty()) {
            criteriaQuery.orderBy(orders);
        }
    }
}
