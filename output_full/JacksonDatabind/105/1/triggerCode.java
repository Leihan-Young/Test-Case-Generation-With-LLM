package com.fasterxml.jackson.databind.deser.jdk;
import java.io.*;
import java.lang.reflect.Array;
import java.util.List;
import org.junit.Assert;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
/**
 * Unit tests for verifying handling of simple basic non-structured
 * types; primitives (and/or their wrappers), Strings.
 */
public class JDKScalarsTest
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
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot coerce empty String");
        }
    }
    private void _verifyEmptyStringFailForPrimitives(String propName) throws IOException
    {
        final ObjectReader reader = MAPPER
                .readerFor(PrimitivesBean.class)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        try {
            reader.readValue(aposToQuotes("{'"+propName+"':''}"));
            fail("Expected failure for boolean + empty String");
        } catch (JsonMappingException e) {
            verifyException(e, "Cannot coerce empty String (\"\")");
            verifyException(e, "to Null value");
        }
    }
    private void verifyPath(MismatchedInputException e, String propName) {
        final List<Reference> path = e.getPath();
        assertEquals(1, path.size());
        assertEquals(propName, path.get(0).getFieldName());
    }
    private void _testNullForPrimitiveArrays(Class<?> cls, Object defValue) throws IOException {
        _testNullForPrimitiveArrays(cls, defValue, true);
    }
    private void _testNullForPrimitiveArrays(Class<?> cls, Object defValue,
            boolean testEmptyString) throws IOException
    {
        final String EMPTY_STRING_JSON = "[ \"\" ]";
        final String JSON_WITH_NULL = "[ null ]";
        final String SIMPLE_NAME = "`"+cls.getSimpleName()+"`";
        final ObjectReader readerCoerceOk = MAPPER.readerFor(cls);
        final ObjectReader readerNoCoerce = readerCoerceOk
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);

        Object ob = readerCoerceOk.forType(cls).readValue(JSON_WITH_NULL);
        assertEquals(1, Array.getLength(ob));
        assertEquals(defValue, Array.get(ob, 0));
        try {
            readerNoCoerce.readValue(JSON_WITH_NULL);
            fail("Should not pass");
        } catch (JsonMappingException e) {
            verifyException(e, "Cannot coerce `null`");
            verifyException(e, "as content of type "+SIMPLE_NAME);
        }
        
        if (testEmptyString) {
            ob = readerCoerceOk.forType(cls).readValue(EMPTY_STRING_JSON);
            assertEquals(1, Array.getLength(ob));
            assertEquals(defValue, Array.get(ob, 0));

            try {
                readerNoCoerce.readValue(EMPTY_STRING_JSON);
                fail("Should not pass");
            } catch (JsonMappingException e) {
                verifyException(e, "Cannot coerce empty String (\"\")");
                verifyException(e, "as content of type "+SIMPLE_NAME);
            }
        }
    }
    private void _testInvalidStringCoercionFail(Class<?> cls) throws IOException
    {
        final String JSON = "[ \"foobar\" ]";
        final String SIMPLE_NAME = cls.getSimpleName();

        try {
            MAPPER.readerFor(cls).readValue(JSON);
            fail("Should not pass");
        } catch (JsonMappingException e) {
            verifyException(e, "Cannot deserialize value of type `"+SIMPLE_NAME+"` from String \"foobar\"");
        }
    }
    public void testVoidDeser() throws Exception
    {
        VoidBean bean = MAPPER.readValue(aposToQuotes("{'value' : 123 }"),
                VoidBean.class);
        assertNull(bean.value);
    }
}
