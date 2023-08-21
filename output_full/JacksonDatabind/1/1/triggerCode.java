package com.fasterxml.jackson.databind.struct;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
public class TestPOJOAsArray extends BaseMapTest
{
    public void testNullColumn() throws Exception
    {
        assertEquals("[null,\"bar\"]", MAPPER.writeValueAsString(new TwoStringsBean()));
    }
}
