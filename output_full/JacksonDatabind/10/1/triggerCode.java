package com.fasterxml.jackson.databind.ser;
import java.io.IOException;
import java.util.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
public class TestAnyGetter extends BaseMapTest
{
    public void testIssue705() throws Exception
    {
        Issue705Bean input = new Issue705Bean("key", "value");        
        String json = MAPPER.writeValueAsString(input);
        assertEquals("{\"stuff\":\"[key/value]\"}", json);
    }
}
