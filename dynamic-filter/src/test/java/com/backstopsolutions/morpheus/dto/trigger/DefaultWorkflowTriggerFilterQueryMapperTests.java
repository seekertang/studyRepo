package com.ethansolutions.morpheus.dto.trigger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ethansolutions.morpheus.dto.filter.FilterLogicType;
import com.ethansolutions.morpheus.dto.filter.FilterNodeDto;
import com.ethansolutions.morpheus.dto.filter.FilterNodeType;
import com.ethansolutions.morpheus.dto.filter.FilterOperator;
import com.ethansolutions.morpheus.dto.filter.FilterQueryDto;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DefaultWorkflowTriggerFilterQueryMapperTests {

    @Test
    void mapsFlatConditionsWithAndFirstPrecedence() {
        DefaultWorkflowTriggerFilterQueryMapper mapper = new DefaultWorkflowTriggerFilterQueryMapper();

        WorkflowTriggerConfigDto config = new WorkflowTriggerConfigDto();
        config.setTriggerType(TriggerType.TIMED);
        config.setEvaluationMode(TriggerEvaluationMode.WHEN_NO_RECORD_MATCHES);
        config.setOperatorPrecedence(TriggerOperatorPrecedence.AND_FIRST);

        WorkflowTriggerTargetDto target = new WorkflowTriggerTargetDto();
        target.setPrimaryEntityType("Strategy");
        target.setActivityType("Call");
        config.setTarget(target);

        RelativeDateValueDto relativeDate = new RelativeDateValueDto();
        relativeDate.setAmount(6);
        relativeDate.setUnit(RelativeDateUnit.MONTH);

        TriggerConditionDto first = new TriggerConditionDto();
        first.setFieldKey("effectiveDate");
        first.setOperator(TriggerFieldOperator.WITHIN_LAST);
        first.setValue(relativeDate);

        TriggerConditionDto second = new TriggerConditionDto();
        second.setJoinWithPrevious(TriggerBooleanOperator.AND);
        second.setFieldKey("template");
        second.setOperator(TriggerFieldOperator.IS);
        second.setValue("MS OMC Form");

        TriggerConditionDto third = new TriggerConditionDto();
        third.setJoinWithPrevious(TriggerBooleanOperator.OR);
        third.setFieldKey("status");
        third.setOperator(TriggerFieldOperator.IS);
        third.setValue("OPEN");

        config.setConditions(List.of(first, second, third));

        WorkflowTriggerMappingContext context = new WorkflowTriggerMappingContext();
        context.setRootEntity("StrategyEntity");
        context.setActivityEntity("MeetingEntity");
        context.setCorrelationParentField("id");
        context.setCorrelationSubField("strategyId");
        context.setActivityTypeField("activityType");
        context.setFieldPathByKey(Map.of(
            "effectiveDate", "effectiveDate",
            "template", "template.name",
            "status", "status"
        ));
        context.setClock(Clock.fixed(Instant.parse("2026-04-24T00:00:00Z"), ZoneOffset.UTC));

        FilterQueryDto query = mapper.map(config, context);

        assertEquals("StrategyEntity", query.getEntity());
        assertEquals(FilterNodeType.EXISTS, query.getFilter().getType());
        assertTrue(Boolean.TRUE.equals(query.getFilter().getNot()));

        FilterNodeDto subFilter = query.getFilter().getSubFilter();
        assertEquals(FilterNodeType.GROUP, subFilter.getType());
        assertEquals(FilterLogicType.AND, subFilter.getLogic());
        assertEquals(2, subFilter.getChildren().size());

        FilterNodeDto logicalNode = subFilter.getChildren().get(1);
        assertEquals(FilterNodeType.GROUP, logicalNode.getType());
        assertEquals(FilterLogicType.OR, logicalNode.getLogic());
        assertEquals(2, logicalNode.getChildren().size());

        FilterNodeDto andGroup = logicalNode.getChildren().get(0);
        assertEquals(FilterLogicType.AND, andGroup.getLogic());
        assertEquals(2, andGroup.getChildren().size());
        assertEquals(FilterOperator.GTE, andGroup.getChildren().get(0).getOperator());
        assertEquals("2025-10-24T00:00", andGroup.getChildren().get(0).getValue().getData());
        assertEquals("template.name", andGroup.getChildren().get(1).getField());

        FilterNodeDto thirdNode = logicalNode.getChildren().get(1);
        assertEquals(FilterOperator.EQ, thirdNode.getOperator());
        assertEquals("status", thirdNode.getField());
        assertEquals("OPEN", thirdNode.getValue().getData());
    }

    @Test
    void mapsConditionTreeWithNestedGroup() {
        DefaultWorkflowTriggerFilterQueryMapper mapper = new DefaultWorkflowTriggerFilterQueryMapper();

        WorkflowTriggerConfigDto config = new WorkflowTriggerConfigDto();
        config.setTriggerType(TriggerType.TIMED);
        config.setEvaluationMode(TriggerEvaluationMode.WHEN_RECORD_MATCHES);

        WorkflowTriggerTargetDto target = new WorkflowTriggerTargetDto();
        target.setPrimaryEntityType("Strategy");
        target.setActivityType("Call");
        config.setTarget(target);

        TriggerConditionTreeNodeDto leftCondition = new TriggerConditionTreeNodeDto();
        leftCondition.setType(TriggerConditionNodeType.CONDITION);
        leftCondition.setFieldKey("template");
        leftCondition.setOperator(TriggerFieldOperator.IS);
        leftCondition.setValue("MS OMC Form");

        TriggerConditionTreeNodeDto rightCondition = new TriggerConditionTreeNodeDto();
        rightCondition.setType(TriggerConditionNodeType.CONDITION);
        rightCondition.setFieldKey("status");
        rightCondition.setOperator(TriggerFieldOperator.IS);
        rightCondition.setValue("OPEN");

        TriggerConditionTreeNodeDto group = new TriggerConditionTreeNodeDto();
        group.setType(TriggerConditionNodeType.GROUP);
        group.setLogic(TriggerBooleanOperator.OR);
        group.setChildren(List.of(leftCondition, rightCondition));
        config.setConditionTree(group);

        WorkflowTriggerMappingContext context = new WorkflowTriggerMappingContext();
        context.setRootEntity("StrategyEntity");
        context.setActivityEntity("MeetingEntity");
        context.setCorrelationParentField("id");
        context.setCorrelationSubField("strategyId");
        context.setActivityTypeField("activityType");
        context.setFieldPathByKey(Map.of(
            "template", "template.name",
            "status", "status"
        ));

        FilterQueryDto query = mapper.map(config, context);

        assertNotNull(query.getFilter());
        assertEquals(FilterNodeType.EXISTS, query.getFilter().getType());
        assertEquals(Boolean.FALSE, query.getFilter().getNot());

        FilterNodeDto subFilter = query.getFilter().getSubFilter();
        assertEquals(FilterLogicType.AND, subFilter.getLogic());

        FilterNodeDto groupNode = subFilter.getChildren().get(1);
        assertEquals(FilterNodeType.GROUP, groupNode.getType());
        assertEquals(FilterLogicType.OR, groupNode.getLogic());
        assertEquals("template.name", groupNode.getChildren().get(0).getField());
        assertEquals("status", groupNode.getChildren().get(1).getField());
    }
}