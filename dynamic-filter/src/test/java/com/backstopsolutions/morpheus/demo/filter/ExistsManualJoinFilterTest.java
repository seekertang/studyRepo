package com.backstopsolutions.morpheus.demo.filter;

import com.backstopsolutions.morpheus.demo.filter.entity.StrategyEntity;
import com.backstopsolutions.morpheus.demo.filter.service.StrategyFilterQueryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class ExistsManualJoinFilterTest {

    private static final String EXISTS_MANUAL_JOIN_EXAMPLE =
        "src/main/java/com/backstopsolutions/morpheus/demo/filter/filter-example-exists-manual-join.json";

    @Autowired
    private StrategyFilterQueryService strategyFilterQueryService;

    @Test
    void shouldMatchActiveStrategiesWithExternalMeetingByManualJoin() {
        List<StrategyEntity> results = strategyFilterQueryService.queryFromJsonFile(EXISTS_MANUAL_JOIN_EXAMPLE);

        List<Long> ids = results.stream()
            .map(StrategyEntity::getId)
            .toList();

        Assertions.assertEquals(2, results.size(), "Expected exactly 2 strategies from data.sql");
        Assertions.assertEquals(List.of(1L, 8L), ids,
            "Expected strategy IDs [1, 8] ordered by updatedAt ASC");
    }

    @Test
    void shouldMatchActiveStrategiesWithExternalMeetingViaDemoMethod() {
        List<StrategyEntity> results = strategyFilterQueryService.queryExistsManualJoinDemo();

        List<Long> ids = results.stream()
            .map(StrategyEntity::getId)
            .toList();

        Assertions.assertEquals(2, results.size(), "Expected exactly 2 strategies from data.sql");
        Assertions.assertEquals(List.of(1L, 8L), ids,
            "Expected strategy IDs [1, 8] ordered by updatedAt ASC");
    }
}
