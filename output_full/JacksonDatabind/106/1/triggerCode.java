package com.fasterxml.jackson.databind.node;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import static org.junit.Assert.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
public class TestTreeTraversingParser
    extends BaseMapTest
{
    public void testNumberOverflowLong() throws IOException
    {
        final BigInteger tooBig = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
        try (final JsonParser p = MAPPER.readTree("[ "+tooBig+" ]").traverse()) {
            assertToken(JsonToken.START_ARRAY, p.nextToken());
            assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(NumberType.BIG_INTEGER, p.getNumberType());
            try {
                p.getLongValue();
                fail("Expected failure for `long` overflow");
            } catch (InputCoercionException e) {
                verifyException(e, "Numeric value ("+tooBig+") out of range of long");
            }
        }
        try (final JsonParser p = MAPPER.readTree("{ \"value\" : "+tooBig+" }").traverse()) {
            assertToken(JsonToken.START_OBJECT, p.nextToken());
            assertToken(JsonToken.FIELD_NAME, p.nextToken());
            assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(NumberType.BIG_INTEGER, p.getNumberType());
            try {
                p.getLongValue();
                fail("Expected failure for `long` overflow");
            } catch (InputCoercionException e) {
                verifyException(e, "Numeric value ("+tooBig+") out of range of long");
            }
        }
        // But also from floating-point
        final String tooBig2 = "1.0e30";
        try (final JsonParser p = MAPPER.readTree("[ "+tooBig2+" ]").traverse()) {
            assertToken(JsonToken.START_ARRAY, p.nextToken());
            assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertEquals(NumberType.DOUBLE, p.getNumberType());
            try {
                p.getLongValue();
                fail("Expected failure for `long` overflow");
            } catch (InputCoercionException e) {
                verifyException(e, "Numeric value ("+tooBig2+") out of range of long");
            }
        }
    }
}
