package com.ethansolutions.morpheus.dto.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FilterQueryJsonParserTests {

    @Test
    void shouldParseAAndBOrCAndDFile() {
        String filePath = "src/main/java/com/ethansolutions/morpheus/dto/filter/filter-example-a-and-b-or-c-and-d.json";

        FilterQueryDto dto = FilterQueryJsonParser.parseFile(filePath);

        Assertions.assertNotNull(dto);
        Assertions.assertNotNull(dto.getFilter());
        Assertions.assertEquals(FilterNodeType.GROUP, dto.getFilter().getType());
        Assertions.assertEquals(FilterLogicType.OR, dto.getFilter().getLogic());
        Assertions.assertEquals(2, dto.getFilter().getChildren().size());
    }

    @Test
    void shouldParseNestedFile() {
        String filePath = "src/main/java/com/ethansolutions/morpheus/dto/filter/filter-example-a-and-b-or-c-and-d-nested.json";

        FilterQueryDto dto = FilterQueryJsonParser.parseFile(filePath);

        Assertions.assertNotNull(dto);
        Assertions.assertNotNull(dto.getFilter());
        Assertions.assertEquals(FilterNodeType.GROUP, dto.getFilter().getType());
        Assertions.assertEquals(FilterLogicType.AND, dto.getFilter().getLogic());
        Assertions.assertEquals(3, dto.getFilter().getChildren().size());
    }
}
