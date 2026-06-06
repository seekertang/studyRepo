package com.backstopsolutions.morpheus.demo.filter.service;

import com.backstopsolutions.morpheus.core.WorkflowRuntimeException;
import com.backstopsolutions.morpheus.demo.filter.entity.MeetingEntity;
import com.backstopsolutions.morpheus.demo.filter.entity.MeetingPartnerEntity;
import com.backstopsolutions.morpheus.demo.filter.entity.MeetingTemplateEntity;
import com.backstopsolutions.morpheus.demo.filter.entity.StrategyEntity;
import com.backstopsolutions.morpheus.demo.filter.repository.StrategyRepository;
import com.backstopsolutions.morpheus.dto.filter.FilterPageDto;
import com.backstopsolutions.morpheus.dto.filter.FilterQueryDto;
import com.backstopsolutions.morpheus.dto.filter.FilterQueryJsonParser;
import com.backstopsolutions.morpheus.dto.filter.engine.FilterBackendType;
import com.backstopsolutions.morpheus.dto.filter.engine.FilterCompilerRegistry;
import com.backstopsolutions.morpheus.dto.filter.engine.JpaFilterSpecificationCompiler;
import com.backstopsolutions.morpheus.dto.filter.engine.MongoFilterCompiler;
import com.backstopsolutions.morpheus.dto.filter.engine.OpenSearchFilterCompiler;
import com.backstopsolutions.morpheus.dto.filter.spec.FilterEntityClassResolver;
import com.backstopsolutions.morpheus.dto.filter.spec.MapFilterEntityClassResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StrategyFilterQueryService {

    private static final String DEMO_BASE_PATH = "src/main/java/com/backstopsolutions/morpheus/demo/filter/";

    private static final String EXAMPLE_EXISTS = DEMO_BASE_PATH + "filter-example-with-exists.json";

    private static final String EXAMPLE_EXISTS_MANUAL_JOIN = DEMO_BASE_PATH + "filter-example-exists-manual-join.json";

    private static final String EXAMPLE_EXISTS_LEFT_JOIN_ASSOC = DEMO_BASE_PATH + "filter-example-with-exists-left-join-association.json";

    private static final String EXAMPLE_MAIN_QUERY_JOIN = DEMO_BASE_PATH + "filter-example-main-query-join.json";

    private static final String EXAMPLE_MAIN_AND_SUB_QUERY_JOINS = DEMO_BASE_PATH + "filter-example-main-and-sub-query-joins.json";

    private static final String EXAMPLE_MAIN_AUTO_PATH_SUB_AUTO_PATH = DEMO_BASE_PATH + "filter-example-main-auto-path-sub-manual-key-join.json";

    private static final String EXAMPLE_OR_NOT_CONDITIONS = DEMO_BASE_PATH + "filter-example-or-not-conditions.json";

    private static final String EXAMPLE_IN_NOT_IN = DEMO_BASE_PATH + "filter-example-in-not-in.json";

    private static final String EXAMPLE_LIKE_ENDS_WITH = DEMO_BASE_PATH + "filter-example-like-ends-with.json";

    private static final String EXAMPLE_GT_IS_NOT_NULL = DEMO_BASE_PATH + "filter-example-gt-is-not-null.json";

    private static final String EXAMPLE_EXISTS_EXTERNAL_MEETING = DEMO_BASE_PATH + "filter-example-exists-external-meeting.json";

    private static final String EXAMPLE_NESTED_AND_OR = DEMO_BASE_PATH + "filter-example-nested-and-or.json";

    private final StrategyRepository strategyRepository;

    public StrategyFilterQueryService(StrategyRepository strategyRepository) {
        this.strategyRepository = strategyRepository;
    }

    @Transactional(readOnly = true)
    public List<StrategyEntity> queryFromJsonFile(String filePath) {
        FilterQueryDto filterQuery = FilterQueryJsonParser.parseFile(filePath);

        Map<String, Class<?>> mapping = new HashMap<>();
        mapping.put("StrategyEntity", StrategyEntity.class);
        mapping.put("MeetingEntity", MeetingEntity.class);
        mapping.put("MeetingTemplateEntity", MeetingTemplateEntity.class);
        mapping.put("MeetingPartnerEntity", MeetingPartnerEntity.class);

        Class<StrategyEntity> rootEntityClass = resolveRootEntityClass(filterQuery, mapping);
        FilterEntityClassResolver resolver = new MapFilterEntityClassResolver(mapping);

        FilterCompilerRegistry registry = new FilterCompilerRegistry();
        registry.register(new JpaFilterSpecificationCompiler<>(rootEntityClass, resolver));
        registry.register(new MongoFilterCompiler());
        registry.register(new OpenSearchFilterCompiler());

        Specification<StrategyEntity> specification = registry
            .<Specification<StrategyEntity>>get(FilterBackendType.JPA)
            .compile(filterQuery, rootEntityClass.getSimpleName());

        Pageable pageable = toPageable(filterQuery);
        if (pageable == null) {
            return strategyRepository.findAll(specification);
        }

        Page<StrategyEntity> page = strategyRepository.findAll(specification, pageable);
        return page.getContent();
    }

    @Transactional(readOnly = true)
    public List<StrategyEntity> queryExistsDemo() {
        return queryFromJsonFile(EXAMPLE_EXISTS);
    }

    @Transactional(readOnly = true)
    public List<StrategyEntity> queryExistsManualJoinDemo() {
        return queryFromJsonFile(EXAMPLE_EXISTS_MANUAL_JOIN);
    }

    @Transactional(readOnly = true)
    public List<StrategyEntity> queryExistsLeftJoinAssociationDemo() {
        return queryFromJsonFile(EXAMPLE_EXISTS_LEFT_JOIN_ASSOC);
    }

    @Transactional(readOnly = true)
    public List<StrategyEntity> queryMainQueryJoinDemo() {
        return queryFromJsonFile(EXAMPLE_MAIN_QUERY_JOIN);
    }

    @Transactional(readOnly = true)
    public List<StrategyEntity> queryMainAndSubQueryJoinsDemo() {
        return queryFromJsonFile(EXAMPLE_MAIN_AND_SUB_QUERY_JOINS);
    }

    @Transactional(readOnly = true)
    public List<StrategyEntity> queryMainAutoPathSubAutoPathDemo() {
        return queryFromJsonFile(EXAMPLE_MAIN_AUTO_PATH_SUB_AUTO_PATH);
    }

    /**
     * Demo: OR / NOT condition combination
     * <p>
     * Query Strategy records that satisfy all of the following conditions:
     * <ol>
     *   <li>status = 'ACTIVE' OR status = 'INACTIVE' (OR group)</li>
     *   <li>aum BETWEEN 1000000 AND 9999999</li>
     *   <li>NOT EXISTS a Meeting correlated by strategyId where the Meeting satisfies:
     *       meetingTemplate.name STARTS_WITH 'Internal'
     *       OR createdAt &lt; 2025-01-01 (OR inside the subquery)</li>
     *   <li>NOT (ownerName IS NULL), meaning ownerName must be non-null</li>
     * </ol>
     * Sort by updatedAt descending and return page 1 with 20 rows.
     */
    @Transactional(readOnly = true)
    public List<StrategyEntity> queryOrNotConditionsDemo() {
        return queryFromJsonFile(EXAMPLE_OR_NOT_CONDITIONS);
    }

    /**
     * Demo: IN / NOT_IN operators
     * status IN ('ACTIVE','INACTIVE') AND id NOT IN (3,4,5)
     */
    @Transactional(readOnly = true)
    public List<StrategyEntity> queryInNotInDemo() {
        return queryFromJsonFile(EXAMPLE_IN_NOT_IN);
    }

    /**
     * Demo: LIKE / ENDS_WITH string matching
     * name LIKE '%Fund%' AND (ownerName ENDS_WITH 'Liu' OR ownerName ENDS_WITH 'Wang')
     */
    @Transactional(readOnly = true)
    public List<StrategyEntity> queryLikeEndsWithDemo() {
        return queryFromJsonFile(EXAMPLE_LIKE_ENDS_WITH);
    }

    /**
     * Demo: GT + IS_NOT_NULL + NE
     * aum > 2000000 AND ownerName IS_NOT_NULL AND status NE 'CLOSED'
     */
    @Transactional(readOnly = true)
    public List<StrategyEntity> queryGtIsNotNullDemo() {
        return queryFromJsonFile(EXAMPLE_GT_IS_NOT_NULL);
    }

    /**
     * Demo: positive EXISTS (ACTIVE strategies with external meetings)
     * status = 'ACTIVE' AND EXISTS Meeting (meetingTemplate.name STARTS_WITH 'External')
     */
    @Transactional(readOnly = true)
    public List<StrategyEntity> queryExistsExternalMeetingDemo() {
        return queryFromJsonFile(EXAMPLE_EXISTS_EXTERNAL_MEETING);
    }

    /**
     * Demo: deeply nested AND/OR
     * (ACTIVE AND aum >= 3000000) OR (INACTIVE AND ownerName STARTS_WITH 'Bob')
     */
    @Transactional(readOnly = true)
    public List<StrategyEntity> queryNestedAndOrDemo() {
        return queryFromJsonFile(EXAMPLE_NESTED_AND_OR);
    }

    @Transactional(readOnly = true)
    public Map<String, List<StrategyEntity>> queryAllDemos() {
        Map<String, List<StrategyEntity>> result = new LinkedHashMap<>();
        result.put("exists", queryExistsDemo());
        result.put("exists_manual_join", queryExistsManualJoinDemo());
        result.put("exists_left_join_association", queryExistsLeftJoinAssociationDemo());
        result.put("main_query_join", queryMainQueryJoinDemo());
        result.put("main_and_sub_query_joins", queryMainAndSubQueryJoinsDemo());
        result.put("main_auto_path_sub_auto_path", queryMainAutoPathSubAutoPathDemo());
        result.put("or_not_conditions", queryOrNotConditionsDemo());
        result.put("in_not_in", queryInNotInDemo());
        result.put("like_ends_with", queryLikeEndsWithDemo());
        result.put("gt_is_not_null", queryGtIsNotNullDemo());
        result.put("exists_external_meeting", queryExistsExternalMeetingDemo());
        result.put("nested_and_or", queryNestedAndOrDemo());
        return result;
    }

    @SuppressWarnings("unchecked")
    private Class<StrategyEntity> resolveRootEntityClass(FilterQueryDto query, Map<String, Class<?>> mapping) {
        WorkflowRuntimeException.check(query != null, "filterQuery is required");
        WorkflowRuntimeException.check(query.getEntity() != null && !query.getEntity().trim().isEmpty(),
                "filterQuery.entity is required");

        Class<?> rootClass = mapping.get(query.getEntity().trim());
        WorkflowRuntimeException.check(rootClass != null,
                "No entity class mapping found for [{0}]", query.getEntity());
        WorkflowRuntimeException.check(StrategyEntity.class.equals(rootClass),
                "This service only supports root entity [Strategy], but got [{0}]", query.getEntity());

        return (Class<StrategyEntity>) rootClass;
    }

    private Pageable toPageable(FilterQueryDto query) {
        if (query == null || query.getPage() == null) {
            return null;
        }

        FilterPageDto page = query.getPage();
        if (page.getIndex() <= 0 || page.getSize() <= 0 || page.getSize() == Integer.MAX_VALUE) {
            return null;
        }

        return PageRequest.of(page.getIndex() - 1, page.getSize());
    }
}
