package com.ethansolutions.morpheus.demo.filter.repository;

import com.ethansolutions.morpheus.demo.filter.entity.StrategyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StrategyRepository extends JpaRepository<StrategyEntity, Long>, JpaSpecificationExecutor<StrategyEntity> {
}
