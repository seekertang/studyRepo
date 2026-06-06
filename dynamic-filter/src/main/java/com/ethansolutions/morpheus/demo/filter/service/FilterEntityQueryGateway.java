package com.ethansolutions.morpheus.demo.filter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface FilterEntityQueryGateway<T> {
    String entityName();

    Class<T> entityClass();

    List<T> findAll(Specification<T> specification, Pageable pageable);
}
