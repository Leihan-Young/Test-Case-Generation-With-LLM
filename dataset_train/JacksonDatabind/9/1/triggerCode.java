package com.fasterxml.jackson.databind.ser;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
public class TestMapSerialization
    extends BaseMapTest
{
    public void testClassKey() throws IOException
    {
        Map<Class<?>,Integer> map = new LinkedHashMap<Class<?>,Integer>();
        map.put(String.class, 2);
        String json = MAPPER.writeValueAsString(map);
        assertEquals(aposToQuotes("{'java.lang.String':2}"), json);
    }
}
