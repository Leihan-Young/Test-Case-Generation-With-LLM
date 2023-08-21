package com.fasterxml.jackson.databind.filter;
import java.io.IOException;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
/**
 * Unit tests for checking that alternative settings for
 * {@link JsonSerialize#include} annotation property work
 * as expected.
 */
public class JsonIncludeTest
    extends BaseMapTest
{
    public void testIssue1351() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        assertEquals(aposToQuotes("{}"), mapper.writeValueAsString(new Issue1351Bean(null, (double) 0)));
        // [databind#1417]
        assertEquals(aposToQuotes("{}"), mapper.writeValueAsString(new Issue1351NonBean(0)));
    }
}
