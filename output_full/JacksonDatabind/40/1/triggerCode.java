package com.fasterxml.jackson.databind.deser;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URI;
import java.util.*;
import org.junit.Assert;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.util.TokenBuffer;
/**
 * Unit tests for verifying handling of simple basic non-structured
 * types; primitives (and/or their wrappers), Strings.
 */
public class TestSimpleTypes
    extends BaseMapTest
{
    private void _testEmptyToNullCoercion(Class<?> primType, Object emptyValue) throws Exception
    {
        final String EMPTY = "\"\"";

        // as per [databind#1095] should only allow coercion from empty String,
        // if `null` is acceptable
        ObjectReader intR = MAPPER.readerFor(primType);
        assertEquals(emptyValue, intR.readValue(EMPTY));
        try {
            intR.with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .readValue("\"\"");
            fail("Should not have passed");
        } catch (JsonMappingException e) {
            verifyException(e, "Can not map Empty String");
        }
    }
    public void testEmptyToNullCoercionForPrimitives() throws Exception {
        _testEmptyToNullCoercion(int.class, Integer.valueOf(0));
        _testEmptyToNullCoercion(long.class, Long.valueOf(0));
        _testEmptyToNullCoercion(double.class, Double.valueOf(0.0));
        _testEmptyToNullCoercion(float.class, Float.valueOf(0.0f));
    }
}
