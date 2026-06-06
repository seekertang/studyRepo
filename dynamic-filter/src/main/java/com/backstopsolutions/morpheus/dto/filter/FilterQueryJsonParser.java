package com.backstopsolutions.morpheus.dto.filter;

import com.backstopsolutions.morpheus.core.WorkflowRuntimeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FilterQueryJsonParser {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private FilterQueryJsonParser() {
    }

    public static FilterQueryDto parseFile(String filePath) {
        WorkflowRuntimeException.check(filePath != null && !filePath.trim().isEmpty(), "filePath is required");

        try {
            String json = Files.readString(Path.of(filePath));
            return parseJson(json);
        } catch (IOException e) {
            throw new WorkflowRuntimeException("Failed to read filter json file [{0}]", filePath);
        }
    }

    public static FilterQueryDto parseJson(String json) {
        WorkflowRuntimeException.check(json != null && !json.trim().isEmpty(), "json content is required");

        try {
            JsonNode root = MAPPER.readTree(json);
            FilterQueryDto filterQuery;
            if (root.has("filterQuery")) {
                filterQuery = MAPPER.treeToValue(root.get("filterQuery"), FilterQueryDto.class);
            } else {
                filterQuery = MAPPER.treeToValue(root, FilterQueryDto.class);
            }

            FilterValidator.validate(filterQuery);
            return filterQuery;
        } catch (JsonProcessingException e) {
            throw new WorkflowRuntimeException("Invalid filter json format: {0}", e.getOriginalMessage());
        }
    }
}
