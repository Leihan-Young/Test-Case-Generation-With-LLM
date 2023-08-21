package com.fasterxml.jackson.databind.exc;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;
public class BasicExceptionTest extends BaseMapTest
{
    public void testLocationAddition() throws Exception
    {
        try {
            /*Map<?,?> map =*/ MAPPER.readValue("{\"value\":\"foo\"}",
                    new TypeReference<Map<ABC, Integer>>() { });
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            String msg = e.getMessage();
            String[] str = msg.split(" at \\[");
            if (str.length != 2) {
                fail("Should only get one 'at [' marker, got "+(str.length-1)+", source: "+msg);
            }
        }
    }
}
