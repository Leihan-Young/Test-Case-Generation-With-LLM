package com.fasterxml.jackson.databind.node;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
/**
 * Basic tests for {@link JsonNode} base class and some features
 * of implementation classes
 */
public class TestJsonNode extends NodeTestBase
{
    public void testArrayWithDefaultTyping() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper()
            .enableDefaultTyping();

        JsonNode array = mapper.readTree("[ 1, 2 ]");
        assertTrue(array.isArray());
        assertEquals(2, array.size());

        JsonNode obj = mapper.readTree("{ \"a\" : 2 }");
        assertTrue(obj.isObject());
        assertEquals(1, obj.size());
        assertEquals(2, obj.path("a").asInt());
    }
}
