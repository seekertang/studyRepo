package com.ethansolutions.morpheus.demo.filter;

import com.ethansolutions.morpheus.demo.filter.entity.StrategyEntity;
import com.ethansolutions.morpheus.demo.filter.service.StrategyFilterQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class OrNotConditionsFilterTest {

    @Autowired
    private StrategyFilterQueryService strategyFilterQueryService;

    @Test
    void testOrNotConditionsFilter() {
        List<StrategyEntity> results = strategyFilterQueryService.queryOrNotConditionsDemo();

        System.out.println("========== OR/NOT Condition Combination Results ==========");
        System.out.println("Found " + results.size() + " record(s):");
        System.out.println("-------------------------------------------");

        if (results.isEmpty()) {
            System.out.println("(No matching data)");
        } else {
            for (int i = 0; i < results.size(); i++) {
                StrategyEntity s = results.get(i);
                System.out.printf("[%d] id=%-5d  name=%-30s  status=%-10s  aum=%-15s  ownerName=%-20s  updatedAt=%s%n",
                        i + 1,
                        s.getId(),
                        s.getName(),
                        s.getStatus(),
                        s.getAum(),
                        s.getOwnerName(),
                        s.getUpdatedAt());
            }
        }

        System.out.println("===========================================");
    }
}

