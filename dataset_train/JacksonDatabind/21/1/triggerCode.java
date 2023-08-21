package com.fasterxml.jackson.databind.deser;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.module.SimpleModule;
public class TestEnumDeserialization
    extends BaseMapTest
{
    public void testEnumWithJsonPropertyRename() throws Exception
    {
        String json = MAPPER.writeValueAsString(new EnumWithPropertyAnno[] {
                EnumWithPropertyAnno.B, EnumWithPropertyAnno.A
        });
        assertEquals("[\"b\",\"a\"]", json);

        // and while not really proper place, let's also verify deser while we're at it
        EnumWithPropertyAnno[] result = MAPPER.readValue(json, EnumWithPropertyAnno[].class);
        assertNotNull(result);
        assertEquals(2, result.length);
        assertSame(EnumWithPropertyAnno.B, result[0]);
        assertSame(EnumWithPropertyAnno.A, result[1]);
    }
}
