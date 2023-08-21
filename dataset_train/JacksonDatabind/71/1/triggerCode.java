package com.fasterxml.jackson.databind.deser;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
public class TestMapDeserialization
    extends BaseMapTest
{
    private void _doTestUntyped(final Map<String, ObjectWrapper> map)
    {
        ObjectWrapper w = map.get("double");
        assertNotNull(w);
        assertEquals(Double.valueOf(42), w.getObject());
        assertEquals("string", map.get("string").getObject());
        assertEquals(Boolean.TRUE, map.get("boolean").getObject());
        assertEquals(Collections.singletonList("list0"), map.get("list").getObject());
        assertTrue(map.containsKey("null"));
        assertNull(map.get("null"));
        assertEquals(5, map.size());
    }
    public void testcharSequenceKeyMap() throws Exception {
        String JSON = aposToQuotes("{'a':'b'}");
        Map<CharSequence,String> result = MAPPER.readValue(JSON, new TypeReference<Map<CharSequence,String>>() { });
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("b", result.get("a"));
    }
}
