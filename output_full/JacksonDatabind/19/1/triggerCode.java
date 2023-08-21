package com.fasterxml.jackson.databind.convert;
import java.util.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
public class TestMapConversions
    extends com.fasterxml.jackson.databind.BaseMapTest
{
    public void testMapToProperties() throws Exception
    {
        Bean bean = new Bean();
        bean.A = 129;
        bean.B = "13";
        Properties props = MAPPER.convertValue(bean, Properties.class);

        assertEquals(2, props.size());

        assertEquals("13", props.getProperty("B"));
        // should coercce non-Strings to Strings
        assertEquals("129", props.getProperty("A"));
    }
}
