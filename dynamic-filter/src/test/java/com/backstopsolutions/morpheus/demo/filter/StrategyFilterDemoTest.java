package com.backstopsolutions.morpheus.demo.filter;

import com.backstopsolutions.morpheus.demo.filter.entity.StrategyEntity;
import com.backstopsolutions.morpheus.demo.filter.service.StrategyFilterQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class StrategyFilterDemoTest {

    @Autowired
    private StrategyFilterQueryService strategyFilterQueryService;

    // ──────────────────────────────────────────────────────────────
    // Helper methods
    // ──────────────────────────────────────────────────────────────

    private void printResults(String title, List<StrategyEntity> results) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.printf ("║  %-56s║%n", title);
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.printf ("║  Total %d record(s)%-43s║%n", results.size(), "");
        System.out.println("╠══╦═══════╦══════════════════════════════╦══════════╦═════════════════╦═══════════════╗");
        System.out.println("║# ║ id    ║ name                         ║ status   ║ aum             ║ ownerName     ║");
        System.out.println("╠══╬═══════╬══════════════════════════════╬══════════╬═════════════════╬═══════════════╣");
        if (results.isEmpty()) {
            System.out.println("║                         (No matching data)                                          ║");
        } else {
            for (int i = 0; i < results.size(); i++) {
                StrategyEntity s = results.get(i);
                System.out.printf("║%-2d║ %-5d ║ %-28s ║ %-8s ║ %-15s ║ %-13s ║%n",
                        i + 1,
                        s.getId(),
                        truncate(s.getName(), 28),
                        s.getStatus(),
                        s.getAum(),
                        truncate(s.getOwnerName(), 13));
            }
        }
        System.out.println("╚══╩═══════╩══════════════════════════════╩══════════╩═════════════════╩═══════════════╝");
    }

    private String truncate(String s, int max) {
        if (s == null) return "NULL";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    // ──────────────────────────────────────────────────────────────
    // Test 1: IN / NOT_IN
    // status IN ('ACTIVE','INACTIVE') AND id NOT IN (3,4,5)
    // Expected matches: id=1,2,6,7,8 (status is valid and id is not in the exclusion set)
    // ──────────────────────────────────────────────────────────────
    @Test
    void testInNotIn() {
        List<StrategyEntity> results = strategyFilterQueryService.queryInNotInDemo();
        printResults("IN / NOT_IN Demo", results);
    }

    // ──────────────────────────────────────────────────────────────
    // Test 2: LIKE / ENDS_WITH
    // name LIKE '%Fund%' AND (ownerName ENDS_WITH 'Liu' OR ENDS_WITH 'Wang')
    // Expected matches: id=1 (Alpha Growth Fund, Alice Wang), id=8 (Theta Clean Fund, Grace Liu)
    // ──────────────────────────────────────────────────────────────
    @Test
    void testLikeEndsWith() {
        List<StrategyEntity> results = strategyFilterQueryService.queryLikeEndsWithDemo();
        printResults("LIKE / ENDS_WITH Demo", results);
    }

    // ──────────────────────────────────────────────────────────────
    // Test 3: GT + IS_NOT_NULL + NE
    // aum > 2000000 AND ownerName IS_NOT_NULL AND status NE 'CLOSED'
    // Expected matches: id=1,4,6,7,8 (aum > 2M, has an owner, not CLOSED; id=2 fails because aum=1.2M)
    // ──────────────────────────────────────────────────────────────
    @Test
    void testGtIsNotNull() {
        List<StrategyEntity> results = strategyFilterQueryService.queryGtIsNotNullDemo();
        printResults("GT + IS_NOT_NULL + NE Demo", results);
    }

    // ──────────────────────────────────────────────────────────────
    // Test 4: Positive EXISTS (ACTIVE strategies with external meetings)
    // status='ACTIVE' AND EXISTS Meeting(meetingTemplate.name STARTS_WITH 'External')
    // Expected matches: id=1 (meeting_template_id=2 External Quarterly), id=8 (same)
    // ──────────────────────────────────────────────────────────────
    @Test
    void testExistsExternalMeeting() {
        List<StrategyEntity> results = strategyFilterQueryService.queryExistsExternalMeetingDemo();
        printResults("Positive EXISTS (External Meeting) Demo", results);
    }

    // ──────────────────────────────────────────────────────────────
    // Test 5: Deeply nested AND/OR
    // (ACTIVE AND aum >= 3000000) OR (INACTIVE AND ownerName STARTS_WITH 'Bob')
    // Expected matches: id=1(ACTIVE,3.5M), id=4(ACTIVE,50M), id=6(ACTIVE,4.5M),
    //           id=7(ACTIVE,300w), id=8(ACTIVE,880w), id=2(INACTIVE,Bob Chen)
    // ──────────────────────────────────────────────────────────────
    @Test
    void testNestedAndOr() {
        List<StrategyEntity> results = strategyFilterQueryService.queryNestedAndOrDemo();
        printResults("Nested AND/OR Demo", results);
    }

    // ──────────────────────────────────────────────────────────────
    // Combined run: execute all demos at once, including the older OR/NOT scenario
    // ──────────────────────────────────────────────────────────────
    @Test
    void testAllDemos() {
        System.out.println("\n\n══════════════ Full Demo Overview ══════════════");

        printResults("1. OR/NOT Condition Combination", strategyFilterQueryService.queryOrNotConditionsDemo());
        printResults("2. IN / NOT_IN", strategyFilterQueryService.queryInNotInDemo());
        printResults("3. LIKE / ENDS_WITH", strategyFilterQueryService.queryLikeEndsWithDemo());
        printResults("4. GT + IS_NOT_NULL + NE", strategyFilterQueryService.queryGtIsNotNullDemo());
        printResults("5. Positive EXISTS (External Meeting)", strategyFilterQueryService.queryExistsExternalMeetingDemo());
        printResults("6. Nested AND/OR", strategyFilterQueryService.queryNestedAndOrDemo());

        System.out.println("\n══════════════ End of Full Demo ══════════════\n");
    }
}

