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
    public void testTooBigBigDecimal() throws Exception
    {
        JsonFactory f = new JsonFactory();
        f.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);

        // 24-Aug-2016, tatu: Initial check limits scale to [-9999,+9999]
        BigDecimal BIG = new BigDecimal("1E+9999");
        BigDecimal TOO_BIG = new BigDecimal("1E+10000");
        BigDecimal SMALL = new BigDecimal("1E-9999");
        BigDecimal TOO_SMALL = new BigDecimal("1E-10000");

        for (boolean useBytes : new boolean[] { false, true } ) {
            for (boolean asString : new boolean[] { false, true } ) {
                JsonGenerator g;
                
                if (useBytes) {
                    g = f.createGenerator(new ByteArrayOutputStream());
                } else {
                    g = f.createGenerator(new StringWriter());
                }
                if (asString) {
                    g.enable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
                }

                // first, ok cases:
                g.writeStartArray();
                g.writeNumber(BIG);
                g.writeNumber(SMALL);
                g.writeEndArray();
                g.close();

                // then invalid
                for (BigDecimal input : new BigDecimal[] { TOO_BIG, TOO_SMALL }) {
                    if (useBytes) {
                        g = f.createGenerator(new ByteArrayOutputStream());
                    } else {
                        g = f.createGenerator(new StringWriter());
                    }
                    if (asString) {
                        g.enable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
                    }
                    try {
                        g.writeNumber(input);
                        fail("Should not have written without exception: "+input);
                    } catch (JsonGenerationException e) {
                        verifyException(e, "Attempt to write plain `java.math.BigDecimal`");
                        verifyException(e, "illegal scale");
                    }
                    g.close();
                }
            }
        }
    }
}
