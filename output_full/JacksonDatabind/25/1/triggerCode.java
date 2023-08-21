package com.fasterxml.jackson.databind.deser;
import java.io.*;
import java.util.*;
import static org.junit.Assert.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
/**
 * This unit test suite tries to verify that the "Native" java type
 * mapper can properly re-construct Java array objects from Json arrays.
 */
public class TestArrayDeserialization
    extends BaseMapTest
{
    /**********************************************************
     */
    
    // for [databind#890]
    public void testByteArrayTypeOverride890() throws Exception
    {
        HiddenBinaryBean890 result = MAPPER.readValue(
                aposToQuotes("{'someBytes':'AQIDBA=='}"), HiddenBinaryBean890.class);
        assertNotNull(result);
        assertNotNull(result.someBytes);
        assertEquals(byte[].class, result.someBytes.getClass());
    }
}
