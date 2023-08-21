package com.fasterxml.jackson.core.json;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.fasterxml.jackson.core.*;
/**
 * Set of basic unit tests for verifying that the basic generator
 * functionality works as expected.
 */
public class TestJsonGeneratorFeatures
    extends com.fasterxml.jackson.core.BaseTest
{
    private final JsonFactory JSON_F = new JsonFactory();
    private String _writeNumbers(JsonFactory jf) throws IOException
    {
        StringWriter sw = new StringWriter();
        JsonGenerator jg = jf.createGenerator(sw);
    
        jg.writeStartArray();
        jg.writeNumber(1);
        jg.writeNumber(2L);
        jg.writeNumber(1.25);
        jg.writeNumber(2.25f);
        jg.writeNumber(BigInteger.valueOf(3001));
        jg.writeNumber(BigDecimal.valueOf(0.5));
        jg.writeNumber("-1");
        jg.writeEndArray();
        jg.close();

        return sw.toString();
    }
    private void _testFieldNameQuotingEnabled(JsonFactory jf, boolean useBytes,
            boolean useQuotes, String exp) throws IOException
    {
        ByteArrayOutputStream bytes = useBytes ? new ByteArrayOutputStream() : null;
        StringWriter sw = useBytes ? null : new StringWriter();
        JsonGenerator gen = useBytes ? jf.createGenerator(bytes) : jf.createGenerator(sw);
        if (useQuotes) {
            gen.enable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
        } else {
            gen.disable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
        }

        gen.writeStartObject();
        gen.writeFieldName("foo");
        gen.writeNumber(1);
        gen.writeEndObject();
        gen.close();

        String json = useBytes ? bytes.toString("UTF-8") : sw.toString();
        assertEquals(exp, json);
    }
    /**********************************************************
     */

    private void _testFieldNameQuoting(JsonFactory jf, boolean quoted)
        throws IOException
    {
        StringWriter sw = new StringWriter();
        JsonGenerator jg = jf.createGenerator(sw);
        jg.writeStartObject();
        jg.writeFieldName("foo");
        jg.writeNumber(1);
        jg.writeEndObject();
        jg.close();

        String result = sw.toString();
        if (quoted) {
            assertEquals("{\"foo\":1}", result);
        } else {
            assertEquals("{foo:1}", result);
        }
    private void _testNonNumericQuoting(JsonFactory jf, boolean quoted)
        throws IOException
    {
        StringWriter sw = new StringWriter();
        JsonGenerator jg = jf.createGenerator(sw);
        jg.writeStartObject();
        jg.writeFieldName("double");
        jg.writeNumber(Double.NaN);
        jg.writeEndObject();
        jg.writeStartObject();
        jg.writeFieldName("float");
        jg.writeNumber(Float.NaN);
        jg.writeEndObject();
        jg.close();
	
        String result = sw.toString();
        if (quoted) {
            assertEquals("{\"double\":\"NaN\"} {\"float\":\"NaN\"}", result);
        } else {
            assertEquals("{\"double\":NaN} {\"float\":NaN}", result);
        }
    }
    private void _testFieldNameQuotingEnabled(JsonFactory jf, boolean useBytes,
            boolean useQuotes, String exp) throws IOException
    {
        ByteArrayOutputStream bytes = useBytes ? new ByteArrayOutputStream() : null;
        StringWriter sw = useBytes ? null : new StringWriter();
        JsonGenerator gen = useBytes ? jf.createGenerator(bytes) : jf.createGenerator(sw);
        if (useQuotes) {
            gen.enable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
        } else {
            gen.disable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
        }

        gen.writeStartObject();
        gen.writeFieldName("foo");
        gen.writeNumber(1);
        gen.writeEndObject();
        gen.close();

        String json = useBytes ? bytes.toString("UTF-8") : sw.toString();
        assertEquals(exp, json);
    }
    public void testFieldNameQuotingEnabled() throws IOException
    {
        // // First, test with default factory, with quoting enabled by default
        
        // First, default, with quotes
        _testFieldNameQuotingEnabled(JSON_F, true, true, "{\"foo\":1}");
        _testFieldNameQuotingEnabled(JSON_F, false, true, "{\"foo\":1}");

        // then without quotes
        _testFieldNameQuotingEnabled(JSON_F, true, false, "{foo:1}");
        _testFieldNameQuotingEnabled(JSON_F, false, false, "{foo:1}");

        // // Then with alternatively configured factory

        JsonFactory JF2 = new JsonFactory();
        JF2.disable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);

        _testFieldNameQuotingEnabled(JF2, true, true, "{\"foo\":1}");
        _testFieldNameQuotingEnabled(JF2, false, true, "{\"foo\":1}");

        // then without quotes
        _testFieldNameQuotingEnabled(JF2, true, false, "{foo:1}");
        _testFieldNameQuotingEnabled(JF2, false, false, "{foo:1}");
    }
}
