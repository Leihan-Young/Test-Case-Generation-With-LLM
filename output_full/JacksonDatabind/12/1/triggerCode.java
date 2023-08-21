package com.fasterxml.jackson.databind.deser;
import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.StdConverter;
/**
 * Test to check that customizations work as expected.
 */
public class TestCustomDeserializers
    extends BaseMapTest
{
    public void testCustomMapValueDeser735() throws Exception {
        String json = "{\"map1\":{\"a\":1},\"map2\":{\"a\":1}}";
        TestMapBean735 bean = MAPPER.readValue(json, TestMapBean735.class);

        assertEquals(100, bean.map1.get("a").intValue());
        assertEquals(1, bean.map2.get("a").intValue());
    }
}
