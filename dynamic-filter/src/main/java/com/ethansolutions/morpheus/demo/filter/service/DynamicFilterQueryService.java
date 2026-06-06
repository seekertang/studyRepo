package com.ethansolutions.morpheus.demo.filter.service;

import com.ethansolutions.morpheus.core.WorkflowRuntimeException;
import com.ethansolutions.morpheus.demo.filter.entity.MeetingEntity;
import com.ethansolutions.morpheus.demo.filter.entity.MeetingTemplateEntity;
import com.ethansolutions.morpheus.dto.filter.FilterPageDto;
import com.ethansolutions.morpheus.dto.filter.FilterQueryDto;
import com.ethansolutions.morpheus.dto.filter.FilterQueryJsonParser;
import com.ethansolutions.morpheus.dto.filter.engine.FilterBackendType;
import com.ethansolutions.morpheus.dto.filter.engine.FilterCompilerRegistry;
import com.ethansolutions.morpheus.dto.filter.engine.JpaFilterSpecificationCompiler;
import com.ethansolutions.morpheus.dto.filter.engine.MongoFilterCompiler;
import com.ethansolutions.morpheus.dto.filter.engine.OpenSearchFilterCompiler;
import com.ethansolutions.morpheus.dto.filter.spec.FilterEntityClassResolver;
import com.ethansolutions.morpheus.dto.filter.spec.MapFilterEntityClassResolver;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DynamicFilterQueryService {

    private final Map<String, FilterEntityQueryGateway<?>> gatewaysByEntity;

    public DynamicFilterQueryService(List<FilterEntityQueryGateway<?>> gateways) {
        this.gatewaysByEntity = new HashMap<>();
        for (FilterEntityQueryGateway<?> gateway : gateways) {
            this.gatewaysByEntity.put(gateway.entityName(), gateway);
        }
    }

    public List<?> queryFromJsonFile(String filePath) {
        FilterQueryDto filterQuery = FilterQueryJsonParser.parseFile(filePath);
        return query(filterQuery);
    }

    public List<?> query(FilterQueryDto filterQuery) {
        WorkflowRuntimeException.check(filterQuery != null, "filterQuery is required");
        WorkflowRuntimeException.check(filterQuery.getEntity() != null && !filterQuery.getEntity().trim().isEmpty(),
                "filterQuery.entity is required");

        String entityName = filterQuery.getEntity().trim();
        FilterEntityQueryGateway<?> gateway = gatewaysByEntity.get(entityName);
        WorkflowRuntimeException.check(gateway != null,
                "No query gateway found for entity [{0}]", entityName);

        FilterEntityClassResolver resolver = new MapFilterEntityClassResolver(buildEntityClassMapping());
        Pageable pageable = toPageable(filterQuery);
        return execute(gateway, filterQuery, resolver, pageable);
    }

    private Map<String, Class<?>> buildEntityClassMapping() {
        Map<String, Class<?>> mapping = new HashMap<>();
        for (Map.Entry<String, FilterEntityQueryGateway<?>> entry : gatewaysByEntity.entrySet()) {
            mapping.put(entry.getKey(), entry.getValue().entityClass());
        }

        // Demo sub-entity mapping used by EXISTS samples.
        mapping.put("Meeting", MeetingEntity.class);
        mapping.put("MeetingTemplate", MeetingTemplateEntity.class);
        return mapping;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> execute(FilterEntityQueryGateway<?> rawGateway,
                                FilterQueryDto filterQuery,
                                FilterEntityClassResolver resolver,
                                Pageable pageable) {
        FilterEntityQueryGateway<T> gateway = (FilterEntityQueryGateway<T>) rawGateway;

        FilterCompilerRegistry registry = new FilterCompilerRegistry();
        registry.register(new JpaFilterSpecificationCompiler<>(gateway.entityClass(), resolver));
        registry.register(new MongoFilterCompiler());
        registry.register(new OpenSearchFilterCompiler());

        Specification<T> specification = registry
                .<Specification<T>>get(FilterBackendType.JPA)
                .compile(filterQuery, gateway.entityClass().getSimpleName());

        return gateway.findAll(specification, pageable);
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
