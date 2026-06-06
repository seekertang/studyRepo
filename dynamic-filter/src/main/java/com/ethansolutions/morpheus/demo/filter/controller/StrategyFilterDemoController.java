package com.ethansolutions.morpheus.demo.filter.controller;

import com.ethansolutions.morpheus.demo.filter.entity.StrategyEntity;
import com.ethansolutions.morpheus.demo.filter.service.StrategyFilterQueryService;
import com.ethansolutions.morpheus.dto.JsonResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/demo/filter/strategy")
public class StrategyFilterDemoController {

    private final StrategyFilterQueryService strategyFilterQueryService;

    public StrategyFilterDemoController(StrategyFilterQueryService strategyFilterQueryService) {
        this.strategyFilterQueryService = strategyFilterQueryService;
    }

    /**
     * Run all demos and return each demo name with its query results.
     * GET /demo/filter/strategy/all
     */
    @GetMapping("/all")
    public JsonResponse<Map<String, List<StrategyEntity>>> queryAll() {
        return JsonResponse.success(strategyFilterQueryService.queryAllDemos());
    }

    /**
     * Basic EXISTS + NOT EXISTS example.
     * GET /demo/filter/strategy/exists
     */
    @GetMapping("/exists")
    public JsonResponse<List<StrategyEntity>> queryExists() {
        return JsonResponse.success(strategyFilterQueryService.queryExistsDemo());
    }

    /**
     * EXISTS + manual key join example.
     * GET /demo/filter/strategy/exists-manual-join
     */
    @GetMapping("/exists-manual-join")
    public JsonResponse<List<StrategyEntity>> queryExistsManualJoin() {
        return JsonResponse.success(strategyFilterQueryService.queryExistsManualJoinDemo());
    }

    /**
     * EXISTS + LEFT JOIN associated property example.
     * GET /demo/filter/strategy/exists-left-join-assoc
     */
    @GetMapping("/exists-left-join-assoc")
    public JsonResponse<List<StrategyEntity>> queryExistsLeftJoinAssoc() {
        return JsonResponse.success(strategyFilterQueryService.queryExistsLeftJoinAssociationDemo());
    }

    /**
     * Main-query join example.
     * GET /demo/filter/strategy/main-join
     */
    @GetMapping("/main-join")
    public JsonResponse<List<StrategyEntity>> queryMainJoin() {
        return JsonResponse.success(strategyFilterQueryService.queryMainQueryJoinDemo());
    }

    /**
     * Example with joins in both the main query and the subquery.
     * GET /demo/filter/strategy/main-and-sub-joins
     */
    @GetMapping("/main-and-sub-joins")
    public JsonResponse<List<StrategyEntity>> queryMainAndSubJoins() {
        return JsonResponse.success(strategyFilterQueryService.queryMainAndSubQueryJoinsDemo());
    }

    /**
     * Example with auto-path join in the main query and manual key join in the subquery.
     * GET /demo/filter/strategy/auto-path-join
     */
    @GetMapping("/auto-path-join")
    public JsonResponse<List<StrategyEntity>> queryAutoPathJoin() {
        return JsonResponse.success(strategyFilterQueryService.queryMainAutoPathSubAutoPathDemo());
    }

    /**
     * OR / NOT condition combination example:
     * - status OR group (ACTIVE | INACTIVE)
     * - aum BETWEEN
     * - NOT EXISTS (OR inside the subquery)
     * - NOT IS_NULL
     * GET /demo/filter/strategy/or-not
     */
    @GetMapping("/or-not")
    public JsonResponse<List<StrategyEntity>> queryOrNotConditions() {
        return JsonResponse.success(strategyFilterQueryService.queryOrNotConditionsDemo());
    }
}

