package com.ethansolutions.morpheus.demo.filter.service;

import com.ethansolutions.morpheus.demo.filter.entity.StrategyEntity;
import com.ethansolutions.morpheus.demo.filter.repository.StrategyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StrategyEntityQueryGateway implements FilterEntityQueryGateway<StrategyEntity> {

    private final StrategyRepository strategyRepository;

    public StrategyEntityQueryGateway(StrategyRepository strategyRepository) {
        this.strategyRepository = strategyRepository;
    }

    @Override
    public String entityName() {
        return "Strategy";
    }

    @Override
    public Class<StrategyEntity> entityClass() {
        return StrategyEntity.class;
    }

    @Override
    public List<StrategyEntity> findAll(Specification<StrategyEntity> specification, Pageable pageable) {
        if (pageable == null) {
            return strategyRepository.findAll(specification);
        }

        Page<StrategyEntity> page = strategyRepository.findAll(specification, pageable);
        return page.getContent();
    }
}
