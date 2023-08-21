package com.fasterxml.jackson.databind.ser;
import java.io.IOException;
import java.util.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
/**
 * This unit test suite tests functioning of {@link JsonValue}
 * annotation with bean serialization.
 */
public class TestJsonValue
    extends BaseMapTest
{
    public void testJsonValueWithCustomOverride() throws Exception
    {
        final Bean838 INPUT = new Bean838();

        // by default, @JsonValue should be used
        assertEquals(quote("value"), MAPPER.writeValueAsString(INPUT));

        // but custom serializer should override it
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule()
            .addSerializer(Bean838.class, new Bean838Serializer())
            );
        assertEquals("42", mapper.writeValueAsString(INPUT));
    }
}
