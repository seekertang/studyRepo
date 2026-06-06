package com.backstopsolutions.morpheus.dto.trigger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowTriggerConfigDto {
    private TriggerType triggerType;

    private TriggerEvaluationMode evaluationMode;

    private WorkflowTriggerTargetDto target;

    private TriggerOperatorPrecedence operatorPrecedence = TriggerOperatorPrecedence.AND_FIRST;

    private List<TriggerConditionDto> conditions;

    private TriggerConditionTreeNodeDto conditionTree;

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public TriggerEvaluationMode getEvaluationMode() {
        return evaluationMode;
    }

    public void setEvaluationMode(TriggerEvaluationMode evaluationMode) {
        this.evaluationMode = evaluationMode;
    }

    public WorkflowTriggerTargetDto getTarget() {
        return target;
    }

    public void setTarget(WorkflowTriggerTargetDto target) {
        this.target = target;
    }

    public TriggerOperatorPrecedence getOperatorPrecedence() {
        return operatorPrecedence;
    }

    public void setOperatorPrecedence(TriggerOperatorPrecedence operatorPrecedence) {
        this.operatorPrecedence = operatorPrecedence;
    }

    public List<TriggerConditionDto> getConditions() {
        return conditions;
    }

    public void setConditions(List<TriggerConditionDto> conditions) {
        this.conditions = conditions;
    }

    public TriggerConditionTreeNodeDto getConditionTree() {
        return conditionTree;
    }

    public void setConditionTree(TriggerConditionTreeNodeDto conditionTree) {
        this.conditionTree = conditionTree;
    }
}