package com.fasterxml.jackson.databind.introspect;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.introspect.TestNamingStrategyCustom.PersonBean;
import com.fasterxml.jackson.databind.node.ObjectNode;
/**
 * Unit tests to verify functioning of 
 * {@link PropertyNamingStrategy#CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES} 
 * and
 * {@link PropertyNamingStrategy#PASCAL_CASE_TO_CAMEL_CASE } 
 * inside the context of an ObjectMapper.
 * PASCAL_CASE_TO_CAMEL_CASE was added in Jackson 2.1,
 * as per [JACKSON-63].
 */
public class TestNamingStrategyStd extends BaseMapTest
{
    /**
     * Test [databind#815], problems with ObjectNode, naming strategy
     */
    public void testNamingWithObjectNode() throws Exception
    {
        ObjectMapper m = new ObjectMapper();
        m.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE);
        ClassWithObjectNodeField result =
            m.readValue(
                "{ \"id\": \"1\", \"json\": { \"foo\": \"bar\", \"baz\": \"bing\" } }",
                ClassWithObjectNodeField.class);
        assertNotNull(result);
        assertEquals("1", result.id);
        assertNotNull(result.json);
        assertEquals(2, result.json.size());
        assertEquals("bing", result.json.path("baz").asText());
    }
}
