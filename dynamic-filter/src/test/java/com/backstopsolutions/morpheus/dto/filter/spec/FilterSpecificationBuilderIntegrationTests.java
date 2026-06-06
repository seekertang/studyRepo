package com.backstopsolutions.morpheus.dto.filter.spec;

import com.backstopsolutions.morpheus.demo.filter.entity.MeetingEntity;
import com.backstopsolutions.morpheus.demo.filter.entity.MeetingTemplateEntity;
import com.backstopsolutions.morpheus.demo.filter.entity.StrategyEntity;
import com.backstopsolutions.morpheus.dto.filter.FilterCorrelationDto;
import com.backstopsolutions.morpheus.dto.filter.FilterLogicType;
import com.backstopsolutions.morpheus.dto.filter.FilterNodeDto;
import com.backstopsolutions.morpheus.dto.filter.FilterNodeType;
import com.backstopsolutions.morpheus.dto.filter.FilterOperator;
import com.backstopsolutions.morpheus.dto.filter.FilterQueryDto;
import com.backstopsolutions.morpheus.dto.filter.FilterValueDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@DataJpaTest(properties = "spring.sql.init.mode=never")
class FilterSpecificationBuilderIntegrationTests {

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUpData() {
        MeetingTemplateEntity reviewTemplate = meetingTemplate(11L, "Quarterly Review");
        MeetingTemplateEntity opsTemplate = meetingTemplate(12L, "Operations");

        entityManager.persist(reviewTemplate);
        entityManager.persist(opsTemplate);

        entityManager.persist(strategy(1L, "Alpha Core", "ACTIVE"));
        entityManager.persist(strategy(2L, "Beta Growth", "ACTIVE"));
        entityManager.persist(strategy(3L, "Gamma Value", "INACTIVE"));

        entityManager.persist(meeting(101L, 1L, reviewTemplate));
        entityManager.persist(meeting(102L, 2L, opsTemplate));

        entityManager.flush();
    }

    @Test
    void shouldApplyGroupConditionAndExistsTogether() {
        FilterNodeDto statusActive = condition("status", FilterOperator.EQ, "ACTIVE");
        FilterNodeDto nameStartsWithAlpha = condition("name", FilterOperator.STARTS_WITH, "Al");
        FilterNodeDto existsReviewMeeting = existsNode(false, condition("meetingTemplate.name", FilterOperator.LIKE, "%Review%"));

        FilterNodeDto orGroup = group(FilterLogicType.OR, nameStartsWithAlpha, existsReviewMeeting);
        FilterNodeDto rootFilter = group(FilterLogicType.AND, statusActive, orGroup);

        FilterQueryDto query = new FilterQueryDto();
        query.setEntity("StrategyEntity");
        query.setFilter(rootFilter);

        Specification<StrategyEntity> specification = FilterSpecificationBuilder.build(
            query,
            StrategyEntity.class,
            new MapFilterEntityClassResolver(Map.of("MeetingEntity", MeetingEntity.class))
        );

        Assertions.assertEquals(List.of(1L), findMatchedStrategyIds(specification));
    }

    @Test
    void shouldSupportNotExistsWithRootCondition() {
        FilterNodeDto statusActive = condition("status", FilterOperator.EQ, "ACTIVE");
        FilterNodeDto notExistsReviewMeeting = existsNode(true, condition("meetingTemplate.name", FilterOperator.LIKE, "%Review%"));
        FilterNodeDto rootFilter = group(FilterLogicType.AND, statusActive, notExistsReviewMeeting);

        FilterQueryDto query = new FilterQueryDto();
        query.setEntity("StrategyEntity");
        query.setFilter(rootFilter);

        Specification<StrategyEntity> specification = FilterSpecificationBuilder.build(
            query,
            StrategyEntity.class,
            new MapFilterEntityClassResolver(Map.of("MeetingEntity", MeetingEntity.class))
        );

        Assertions.assertEquals(List.of(2L), findMatchedStrategyIds(specification));
    }

    private List<Long> findMatchedStrategyIds(Specification<StrategyEntity> specification) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StrategyEntity> criteriaQuery = cb.createQuery(StrategyEntity.class);
        Root<StrategyEntity> root = criteriaQuery.from(StrategyEntity.class);

        Predicate predicate = specification.toPredicate(root, criteriaQuery, cb);
        if (predicate != null) {
            criteriaQuery.where(predicate);
        }

        criteriaQuery.select(root).orderBy(cb.asc(root.get("id")));
        return entityManager.createQuery(criteriaQuery)
            .getResultList()
            .stream()
            .map(StrategyEntity::getId)
            .toList();
    }

    private static FilterNodeDto group(FilterLogicType logic, FilterNodeDto... children) {
        FilterNodeDto node = new FilterNodeDto();
        node.setType(FilterNodeType.GROUP);
        node.setLogic(logic);
        node.setChildren(List.of(children));
        return node;
    }

    private static FilterNodeDto condition(String field, FilterOperator operator, Object valueData) {
        FilterNodeDto node = new FilterNodeDto();
        node.setType(FilterNodeType.CONDITION);
        node.setField(field);
        node.setOperator(operator);

        FilterValueDto value = new FilterValueDto();
        value.setData(valueData);
        node.setValue(value);

        return node;
    }

    private static FilterNodeDto existsNode(boolean not, FilterNodeDto subFilter) {
        FilterNodeDto node = new FilterNodeDto();
        node.setType(FilterNodeType.EXISTS);
        node.setSubEntity("MeetingEntity");
        node.setNot(not);

        FilterCorrelationDto correlation = new FilterCorrelationDto();
        correlation.setParentField("id");
        correlation.setSubField("strategyId");
        node.setCorrelation(correlation);

        node.setSubFilter(subFilter);
        return node;
    }

    private static StrategyEntity strategy(Long id, String name, String status) {
        StrategyEntity strategy = new StrategyEntity();
        strategy.setId(id);
        strategy.setName(name);
        strategy.setStatus(status);
        strategy.setUpdatedAt(LocalDateTime.now());
        return strategy;
    }

    private static MeetingTemplateEntity meetingTemplate(Long id, String name) {
        MeetingTemplateEntity template = new MeetingTemplateEntity();
        template.setId(id);
        template.setName(name);
        return template;
    }

    private static MeetingEntity meeting(Long id, Long strategyId, MeetingTemplateEntity template) {
        MeetingEntity meeting = new MeetingEntity();
        meeting.setId(id);
        meeting.setStrategyId(strategyId);
        meeting.setMeetingTemplate(template);
        meeting.setCreatedAt(LocalDateTime.now());
        return meeting;
    }
}
