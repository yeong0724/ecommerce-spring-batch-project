package com.ecommerce.batch.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ReflectionUtilsTest {
    private static class TestClass {

        private String field1;
        private int field2;
        public static final String CONSTANT = "constant";
    }

    @Test
    void testGetFieldNames() {
        List<String> fieldNames = ReflectionUtils.getFieldNames(TestClass.class);

        assertAll(
                () -> assertThat(fieldNames).hasSize(2),
                () -> assertThat(fieldNames).containsExactly("field1", "field2"),
                () -> assertThat(fieldNames).doesNotContain("CONSTANT")
        );
    }
}