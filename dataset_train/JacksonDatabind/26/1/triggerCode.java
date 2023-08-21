package com.fasterxml.jackson.databind;
import java.io.*;
import java.util.*;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.LRUMap;
/**
 * Tests to verify that most core Jackson components can be serialized
 * using default JDK serialization: this feature is useful for some
 * platforms, such as Android, where memory management is handled
 * much more aggressively.
 */
public class TestJDKSerialization extends BaseMapTest
{
    public void testEnumHandlers() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        // ensure we have serializers and/or deserializers, first
        String json = mapper.writerFor(EnumPOJO.class)
                .writeValueAsString(new EnumPOJO());
        EnumPOJO result = mapper.readerFor(EnumPOJO.class)
                .readValue(json);
        assertNotNull(result);

        // and then use JDK serialization to freeze/thaw objects
        byte[] bytes = jdkSerialize(mapper);
        ObjectMapper mapper2 = jdkDeserialize(bytes);
        assertNotNull(mapper2);

        bytes = jdkSerialize(mapper.readerFor(EnumPOJO.class));
        ObjectReader r = jdkDeserialize(bytes);
        assertNotNull(r);

        /* 14-Aug-2015, tatu: Looks like pre-loading JsonSerializer is problematic
         *    at this point; comment out for now. Try to fix later on.
         */
        bytes = jdkSerialize(mapper.writerFor(EnumPOJO.class));
        ObjectWriter w = jdkDeserialize(bytes);
        assertNotNull(w);

        // plus, ensure objects are usable:
        String json2 = w.writeValueAsString(new EnumPOJO());
        assertEquals(json, json2);
        EnumPOJO result2 = r.readValue(json2);
        assertNotNull(result2);
    }
}
