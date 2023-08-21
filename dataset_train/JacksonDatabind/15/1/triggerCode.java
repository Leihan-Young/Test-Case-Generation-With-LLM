package com.fasterxml.jackson.databind.convert;
import java.io.IOException;
import java.util.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
public class TestConvertingSerializer
    extends com.fasterxml.jackson.databind.BaseMapTest
{
    public void testIssue731() throws Exception
    {
        String json = objectWriter().writeValueAsString(new ConvertingBeanWithUntypedConverter(1, 2));
        // must be  {"a":2,"b":4}
        assertEquals("{\"a\":2,\"b\":4}", json);
    }
}
