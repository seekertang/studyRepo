package com.backstopsolutions.morpheus.dto.trigger;

import com.backstopsolutions.morpheus.core.WorkflowRuntimeException;
import com.backstopsolutions.morpheus.dto.filter.FilterCorrelationDto;
import com.backstopsolutions.morpheus.dto.filter.FilterLogicType;
import com.backstopsolutions.morpheus.dto.filter.FilterNodeDto;
import com.backstopsolutions.morpheus.dto.filter.FilterNodeType;
import com.backstopsolutions.morpheus.dto.filter.FilterOperator;
import com.backstopsolutions.morpheus.dto.filter.FilterQueryDto;
import com.backstopsolutions.morpheus.dto.filter.FilterValueDto;
import com.backstopsolutions.morpheus.dto.filter.FilterValueKind;
import com.backstopsolutions.morpheus.dto.filter.FilterValidator;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DefaultWorkflowTriggerFilterQueryMapper implements WorkflowTriggerFilterQueryMapper {

    @Override
    public FilterQueryDto map(WorkflowTriggerConfigDto config, WorkflowTriggerMappingContext context) {
        validateInputs(config, context);

        FilterQueryDto query = new FilterQueryDto();
        query.setEntity(context.getRootEntity());
        query.setFilter(buildExistsNode(config, context));

        FilterValidator.validate(query);
        return query;
    }

    private void validateInputs(WorkflowTriggerConfigDto config, WorkflowTriggerMappingContext context) {
        WorkflowRuntimeException.check(config != null, "workflow trigger config is required");
        WorkflowRuntimeException.check(config.getEvaluationMode() != null, "evaluationMode is required");
        WorkflowRuntimeException.check(config.getTarget() != null, "target is required");
        WorkflowRuntimeException.check(hasText(config.getTarget().getPrimaryEntityType()), "target.primaryEntityType is required");
        WorkflowRuntimeException.check(context != null, "workflow trigger mapping context is required");
        WorkflowRuntimeException.check(hasText(context.getRootEntity()), "mappingContext.rootEntity is required");
        WorkflowRuntimeException.check(hasText(context.getActivityEntity()), "mappingContext.activityEntity is required");
        WorkflowRuntimeException.check(hasText(context.getCorrelationParentField()), "mappingContext.correlationParentField is required");
        WorkflowRuntimeException.check(hasText(context.getCorrelationSubField()), "mappingContext.correlationSubField is required");
        WorkflowRuntimeException.check(config.getConditionTree() != null || (config.getConditions() != null && !config.getConditions().isEmpty()),
            "Either conditionTree or conditions is required");
    }

    private FilterNodeDto buildExistsNode(WorkflowTriggerConfigDto config, WorkflowTriggerMappingContext context) {
        FilterNodeDto existsNode = new FilterNodeDto();
        existsNode.setType(FilterNodeType.EXISTS);
        existsNode.setSubEntity(context.getActivityEntity());
        existsNode.setNot(config.getEvaluationMode() == TriggerEvaluationMode.WHEN_NO_RECORD_MATCHES);

        FilterCorrelationDto correlation = new FilterCorrelationDto();
        correlation.setParentField(context.getCorrelationParentField());
        correlation.setSubField(context.getCorrelationSubField());
        existsNode.setCorrelation(correlation);
        existsNode.setSubFilter(buildSubFilter(config, context));
        return existsNode;
    }

    private FilterNodeDto buildSubFilter(WorkflowTriggerConfigDto config, WorkflowTriggerMappingContext context) {
        List<FilterNodeDto> children = new ArrayList<>();

        if (hasText(context.getActivityTypeField()) && hasText(config.getTarget().getActivityType())) {
            children.add(buildCondition(context.getActivityTypeField(), FilterOperator.EQ, config.getTarget().getActivityType()));
        }

        FilterNodeDto triggerConditionNode = config.getConditionTree() != null
            ? mapTreeNode(config.getConditionTree(), context)
            : mapLinearConditions(config, context);
        children.add(triggerConditionNode);

        if (children.size() == 1) {
            return children.get(0);
        }

        FilterNodeDto group = new FilterNodeDto();
        group.setType(FilterNodeType.GROUP);
        group.setLogic(FilterLogicType.AND);
        group.setChildren(children);
        return group;
    }

    private FilterNodeDto mapLinearConditions(WorkflowTriggerConfigDto config, WorkflowTriggerMappingContext context) {
        List<TriggerConditionDto> conditions = config.getConditions();
        WorkflowRuntimeException.check(conditions != null && !conditions.isEmpty(), "conditions must not be empty");

        if (config.getOperatorPrecedence() == TriggerOperatorPrecedence.LEFT_TO_RIGHT) {
            return mapLeftToRight(conditions, context);
        }
        return mapAndFirst(conditions, context);
    }

    private FilterNodeDto mapLeftToRight(List<TriggerConditionDto> conditions, WorkflowTriggerMappingContext context) {
        FilterNodeDto current = mapCondition(conditions.get(0), context);
        for (int index = 1; index < conditions.size(); index++) {
            TriggerConditionDto nextCondition = conditions.get(index);
            TriggerBooleanOperator join = requireJoin(nextCondition, index);
            current = combine(join, current, mapCondition(nextCondition, context));
        }
        return current;
    }

    private FilterNodeDto mapAndFirst(List<TriggerConditionDto> conditions, WorkflowTriggerMappingContext context) {
        List<FilterNodeDto> orGroups = new ArrayList<>();
        List<FilterNodeDto> currentAndGroup = new ArrayList<>();
        currentAndGroup.add(mapCondition(conditions.get(0), context));

        for (int index = 1; index < conditions.size(); index++) {
            TriggerConditionDto condition = conditions.get(index);
            TriggerBooleanOperator join = requireJoin(condition, index);
            FilterNodeDto mapped = mapCondition(condition, context);

            if (join == TriggerBooleanOperator.AND) {
                currentAndGroup.add(mapped);
            } else if (join == TriggerBooleanOperator.OR) {
                orGroups.add(asGroup(FilterLogicType.AND, currentAndGroup));
                currentAndGroup = new ArrayList<>();
                currentAndGroup.add(mapped);
            } else {
                throw new WorkflowRuntimeException("NOT is not supported in flat conditions; use conditionTree instead");
            }
        }

        orGroups.add(asGroup(FilterLogicType.AND, currentAndGroup));
        return asGroup(FilterLogicType.OR, orGroups);
    }

    private FilterNodeDto mapTreeNode(TriggerConditionTreeNodeDto node, WorkflowTriggerMappingContext context) {
        WorkflowRuntimeException.check(node != null, "conditionTree node is required");
        WorkflowRuntimeException.check(node.getType() != null, "conditionTree.type is required");

        if (node.getType() == TriggerConditionNodeType.CONDITION) {
            TriggerConditionDto condition = new TriggerConditionDto();
            condition.setFieldKey(node.getFieldKey());
            condition.setFieldLabel(node.getFieldLabel());
            condition.setOperator(node.getOperator());
            condition.setValue(node.getValue());
            return mapCondition(condition, context);
        }

        WorkflowRuntimeException.check(node.getLogic() != null, "conditionTree.logic is required for GROUP");
        WorkflowRuntimeException.check(node.getChildren() != null && !node.getChildren().isEmpty(),
            "conditionTree.children must not be empty for GROUP");

        List<FilterNodeDto> children = new ArrayList<>();
        for (TriggerConditionTreeNodeDto child : node.getChildren()) {
            children.add(mapTreeNode(child, context));
        }

        if (node.getLogic() == TriggerBooleanOperator.NOT) {
            WorkflowRuntimeException.check(children.size() == 1,
                "conditionTree NOT group must contain exactly one child");
            FilterNodeDto notNode = new FilterNodeDto();
            notNode.setType(FilterNodeType.GROUP);
            notNode.setLogic(FilterLogicType.NOT);
            notNode.setChildren(children);
            return notNode;
        }

        return asGroup(node.getLogic() == TriggerBooleanOperator.OR ? FilterLogicType.OR : FilterLogicType.AND, children);
    }

    private FilterNodeDto mapCondition(TriggerConditionDto condition, WorkflowTriggerMappingContext context) {
        WorkflowRuntimeException.check(condition != null, "condition is required");
        WorkflowRuntimeException.check(hasText(condition.getFieldKey()), "condition.fieldKey is required");
        WorkflowRuntimeException.check(condition.getOperator() != null,
            "condition.operator is required for fieldKey [{0}]",
            condition.getFieldKey());

        String fieldPath = context.requireFieldPath(condition.getFieldKey());
        TriggerFieldOperator operator = condition.getOperator();

        return switch (operator) {
            case IS -> buildCondition(fieldPath, FilterOperator.EQ, condition.getValue());
            case IS_NOT -> buildCondition(fieldPath, FilterOperator.NE, condition.getValue());
            case GREATER_THAN -> buildCondition(fieldPath, FilterOperator.GT, condition.getValue());
            case GREATER_THAN_OR_EQUAL -> buildCondition(fieldPath, FilterOperator.GTE, condition.getValue());
            case LESS_THAN -> buildCondition(fieldPath, FilterOperator.LT, condition.getValue());
            case LESS_THAN_OR_EQUAL -> buildCondition(fieldPath, FilterOperator.LTE, condition.getValue());
            case IN -> buildCondition(fieldPath, FilterOperator.IN, condition.getValue());
            case NOT_IN -> buildCondition(fieldPath, FilterOperator.NOT_IN, condition.getValue());
            case BETWEEN -> buildCondition(fieldPath, FilterOperator.BETWEEN, condition.getValue());
            case CONTAINS -> buildCondition(fieldPath, FilterOperator.LIKE, condition.getValue());
            case STARTS_WITH -> buildCondition(fieldPath, FilterOperator.STARTS_WITH, condition.getValue());
            case ENDS_WITH -> buildCondition(fieldPath, FilterOperator.ENDS_WITH, condition.getValue());
            case IS_EMPTY -> buildCondition(fieldPath, FilterOperator.IS_NULL, null);
            case IS_NOT_EMPTY -> buildCondition(fieldPath, FilterOperator.IS_NOT_NULL, null);
            case WITHIN_LAST -> buildCondition(fieldPath, FilterOperator.GTE, toRelativeDateLowerBound(condition.getValue(), context.getClock()));
        };
    }

    private Object toRelativeDateLowerBound(Object rawValue, Clock clock) {
        WorkflowRuntimeException.check(rawValue instanceof RelativeDateValueDto,
            "WITHIN_LAST value must be RelativeDateValueDto");

        RelativeDateValueDto value = (RelativeDateValueDto) rawValue;
        WorkflowRuntimeException.check(value.getAmount() != null && value.getAmount() > 0,
            "WITHIN_LAST amount must be greater than 0");
        WorkflowRuntimeException.check(value.getUnit() != null,
            "WITHIN_LAST unit is required");

        LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
        LocalDateTime lowerBound = switch (value.getUnit()) {
            case MINUTE -> now.minus(value.getAmount(), ChronoUnit.MINUTES);
            case HOUR -> now.minus(value.getAmount(), ChronoUnit.HOURS);
            case DAY -> now.minus(value.getAmount(), ChronoUnit.DAYS);
            case WEEK -> now.minus(value.getAmount(), ChronoUnit.WEEKS);
            case MONTH -> now.minus(value.getAmount(), ChronoUnit.MONTHS);
            case YEAR -> now.minus(value.getAmount(), ChronoUnit.YEARS);
        };
        return lowerBound.toString();
    }

    private TriggerBooleanOperator requireJoin(TriggerConditionDto condition, int index) {
        WorkflowRuntimeException.check(condition.getJoinWithPrevious() != null,
            "conditions[{0}].joinWithPrevious is required",
            index);
        return condition.getJoinWithPrevious();
    }

    private FilterNodeDto combine(TriggerBooleanOperator join, FilterNodeDto left, FilterNodeDto right) {
        if (join == TriggerBooleanOperator.NOT) {
            throw new WorkflowRuntimeException("NOT is not supported as joinWithPrevious; use conditionTree instead");
        }
        return asGroup(join == TriggerBooleanOperator.OR ? FilterLogicType.OR : FilterLogicType.AND,
            List.of(left, right));
    }

    private FilterNodeDto asGroup(FilterLogicType logic, List<FilterNodeDto> nodes) {
        if (nodes.size() == 1) {
            return nodes.get(0);
        }
        FilterNodeDto group = new FilterNodeDto();
        group.setType(FilterNodeType.GROUP);
        group.setLogic(logic);
        group.setChildren(nodes);
        return group;
    }

    private FilterNodeDto buildCondition(String field, FilterOperator operator, Object data) {
        FilterNodeDto node = new FilterNodeDto();
        node.setType(FilterNodeType.CONDITION);
        node.setField(field);
        node.setOperator(operator);

        if (operator.requiresValue()) {
            FilterValueDto value = new FilterValueDto();
            value.setKind(FilterValueKind.LITERAL);
            value.setData(data);
            node.setValue(value);
        }

        return node;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}