package com.fasterxml.jackson.databind.ser;
import java.io.IOException;
import java.util.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
public class TestJsonSerialize2
    extends BaseMapTest
{
    public void testEmptyInclusionScalars() throws IOException
    {
        ObjectMapper defMapper = MAPPER;
        ObjectMapper inclMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        // First, Strings
        StringWrapper str = new StringWrapper("");
        assertEquals("{\"str\":\"\"}", defMapper.writeValueAsString(str));
        assertEquals("{}", inclMapper.writeValueAsString(str));
        assertEquals("{}", inclMapper.writeValueAsString(new StringWrapper()));

        assertEquals("{\"value\":\"x\"}", defMapper.writeValueAsString(new NonEmptyString("x")));
        assertEquals("{}", defMapper.writeValueAsString(new NonEmptyString("")));

        // Then numbers
        assertEquals("{\"value\":12}", defMapper.writeValueAsString(new NonEmptyInt(12)));
        assertEquals("{}", defMapper.writeValueAsString(new NonEmptyInt(0)));

        assertEquals("{\"value\":1.25}", defMapper.writeValueAsString(new NonEmptyDouble(1.25)));
        assertEquals("{}", defMapper.writeValueAsString(new NonEmptyDouble(0.0)));

        IntWrapper zero = new IntWrapper(0);
        assertEquals("{\"i\":0}", defMapper.writeValueAsString(zero));
        assertEquals("{}", inclMapper.writeValueAsString(zero));
    }
}
