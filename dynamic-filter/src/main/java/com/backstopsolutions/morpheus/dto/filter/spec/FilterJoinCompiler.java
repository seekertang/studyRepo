package com.backstopsolutions.morpheus.dto.filter.spec;

import com.backstopsolutions.morpheus.core.WorkflowRuntimeException;
import com.backstopsolutions.morpheus.dto.filter.FilterJoinDto;
import com.backstopsolutions.morpheus.dto.filter.FilterNodeDto;
import com.backstopsolutions.morpheus.dto.filter.FilterQueryDto;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.hibernate.query.criteria.JpaEntityJoin;
import org.hibernate.query.criteria.JpaFrom;
import org.hibernate.query.sqm.tree.SqmJoinType;

import java.util.ArrayList;
import java.util.List;

final class FilterJoinCompiler {

    private final FilterPathResolver pathResolver;
    private final FilterEntityClassResolver entityClassResolver;

    FilterJoinCompiler(FilterPathResolver pathResolver, FilterEntityClassResolver entityClassResolver) {
        this.pathResolver = pathResolver;
        this.entityClassResolver = entityClassResolver;
    }

    List<Predicate> compileRootJoins(FilterQueryDto query, CriteriaQuery<?> criteriaQuery,
                                     FilterCompileContext rootContext, CriteriaBuilder cb) {
        return compileJoins(query.getJoins(), rootContext, cb, criteriaQuery::from);
    }

    List<Predicate> compileSubqueryJoins(FilterNodeDto node, Subquery<Long> subquery,
                                         FilterCompileContext subContext, CriteriaBuilder cb) {
        return compileJoins(node.getJoins(), subContext, cb, subquery::from);
    }

    private List<Predicate> compileJoins(List<FilterJoinDto> joins,
                                         FilterCompileContext context,
                                         CriteriaBuilder cb,
                                         RootFromFactory rootFromFactory) {
        List<Predicate> predicates = new ArrayList<>();
        if (joins == null || joins.isEmpty()) {
            return predicates;
        }

        for (FilterJoinDto join : joins) {
            Join<?, ?> associationJoin = tryCreateAssociationJoin(context.getBaseFrom(), join);
            if (associationJoin != null) {
                context.putAlias(join.getAlias(), associationJoin);
                continue;
            }

            Class<?> targetEntityClass = entityClassResolver.resolve(join.getTargetEntity());
            WorkflowRuntimeException.check(targetEntityClass != null,
                "No mapped entity class for join targetEntity [{0}]", join.getTargetEntity());

            WorkflowRuntimeException.check(!isLeftJoin(join),
                "LEFT join for manual key-based join [{0}] is not supported yet. " +
                    "Use association sourceField or INNER join.", join.getAlias());

            JpaEntityJoin<?> entityJoin = tryCreateEntityJoin(context.getBaseFrom(), targetEntityClass, join);
            if (entityJoin != null) {
                Path<?> sourcePath = pathResolver.resolvePath(context, join.getSourceField());
                Path<?> targetPath = pathResolver.resolvePath(FilterCompileContext.of(entityJoin), join.getTargetField());
                entityJoin.on(cb.equal(sourcePath, targetPath));
                context.putAlias(join.getAlias(), entityJoin);
                continue;
            }

            Root<?> targetRoot = rootFromFactory.from(targetEntityClass);
            context.putAlias(join.getAlias(), targetRoot);

            Path<?> sourcePath = pathResolver.resolvePath(context, join.getSourceField());
            Path<?> targetPath = pathResolver.resolvePath(FilterCompileContext.of(targetRoot), join.getTargetField());
            predicates.add(cb.equal(sourcePath, targetPath));
        }

        return predicates;
    }

    @FunctionalInterface
    private interface RootFromFactory {
        Root<?> from(Class<?> entityClass);
    }

    private Join<?, ?> tryCreateAssociationJoin(From<?, ?> baseFrom, FilterJoinDto join) {
        String sourceField = join.getSourceField();
        if (sourceField == null || sourceField.trim().isEmpty()) {
            return null;
        }

        try {
            return joinByPath(baseFrom, sourceField, toJpaJoinType(join));
        } catch (RuntimeException ex) {
            return null;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private JpaEntityJoin<?> tryCreateEntityJoin(From<?, ?> baseFrom, Class<?> targetEntityClass, FilterJoinDto join) {
        if (!(baseFrom instanceof JpaFrom<?, ?> jpaFrom)) {
            return null;
        }

        try {
            return jpaFrom.join((Class) targetEntityClass, toSqmJoinType(join));
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private Join<?, ?> joinByPath(From<?, ?> baseFrom, String sourceFieldPath, JoinType finalJoinType) {
        String[] segments = sourceFieldPath.split("\\.");
        WorkflowRuntimeException.check(segments.length > 0,
            "Invalid join sourceField path [{0}]", sourceFieldPath);

        From<?, ?> current = baseFrom;
        for (int i = 0; i < segments.length - 1; i++) {
            current = current.join(segments[i], JoinType.INNER);
        }

        return current.join(segments[segments.length - 1], finalJoinType);
    }

    private JoinType toJpaJoinType(FilterJoinDto join) {
        return isLeftJoin(join) ? JoinType.LEFT : JoinType.INNER;
    }

    private SqmJoinType toSqmJoinType(FilterJoinDto join) {
        return isLeftJoin(join) ? SqmJoinType.LEFT : SqmJoinType.INNER;
    }

    private boolean isLeftJoin(FilterJoinDto join) {
        return join.getJoinType() != null && "LEFT".equals(join.getJoinType().name());
    }
}