package com.fasterxml.jackson.databind.node;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.Assert;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
/**
 * Unit tests for verifying functionality of {@link JsonNode} methods that
 * convert values to other types
 */
public class TestConversions extends BaseMapTest
{
    public void testConversionOfPojos() throws Exception
    {
        final Issue467Bean input = new Issue467Bean(13);
        final String EXP = "{\"x\":13}";
        
        // first, sanity check
        String json = MAPPER.writeValueAsString(input);
        assertEquals(EXP, json);

        // then via conversions: should become JSON Object
        JsonNode tree = MAPPER.valueToTree(input);
        assertTrue("Expected Object, got "+tree.getNodeType(), tree.isObject());
        assertEquals(EXP, MAPPER.writeValueAsString(tree));
    }
}
