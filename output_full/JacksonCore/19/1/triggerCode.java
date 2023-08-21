package com.fasterxml.jackson.core.json;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.fasterxml.jackson.core.*;
/**
 * Set of basic unit tests for verifying that the basic parser
 * functionality works as expected.
 */
public class TestNumericValues
    extends com.fasterxml.jackson.core.BaseTest
{
    private final JsonFactory FACTORY = new JsonFactory();
    private void _testSimpleInt(int EXP_I, boolean useStream) throws Exception
    {
        String DOC = "[ "+EXP_I+" ]";
        JsonParser jp = useStream
                ? FACTORY.createParser(DOC)
                : FACTORY.createParser(DOC.getBytes("UTF-8"));
        assertToken(JsonToken.START_ARRAY, jp.nextToken());
        assertToken(JsonToken.VALUE_NUMBER_INT, jp.nextToken());
        assertEquals(JsonParser.NumberType.INT, jp.getNumberType());
        assertEquals(""+EXP_I, jp.getText());

        assertEquals(EXP_I, jp.getIntValue());
        assertEquals((long) EXP_I, jp.getLongValue());
        assertEquals((double) EXP_I, jp.getDoubleValue());
        assertEquals(BigDecimal.valueOf((long) EXP_I), jp.getDecimalValue());
        assertToken(JsonToken.END_ARRAY, jp.nextToken());
        assertNull(jp.nextToken());
        jp.close();

        DOC = String.valueOf(EXP_I);
        jp = useStream
                ? FACTORY.createParser(DOC)
                : FACTORY.createParser(DOC.getBytes("UTF-8"));
        assertToken(JsonToken.VALUE_NUMBER_INT, jp.nextToken());
        assertEquals(DOC, jp.getText());

        int i = 0;
        
        try {
            i = jp.getIntValue();
        } catch (Exception e) {
            throw new Exception("Failed to parse input '"+DOC+"' (parser of type "+jp.getClass().getSimpleName()+")", e);
        }
        
        assertEquals(EXP_I, i);

        assertEquals((long) EXP_I, jp.getLongValue());
        assertEquals((double) EXP_I, jp.getDoubleValue());
        assertEquals(BigDecimal.valueOf((long) EXP_I), jp.getDecimalValue());
        assertNull(jp.nextToken());
        jp.close();
    }
    private void _testLongNumbers(JsonFactory f, String num, boolean useStream) throws Exception
    {
        final String doc = "[ "+num+" ]";
        JsonParser jp = useStream
                ? f.createParser(doc.getBytes("UTF-8"))
                        : f.createParser(doc);
        assertToken(JsonToken.START_ARRAY, jp.nextToken());
        assertToken(JsonToken.VALUE_NUMBER_INT, jp.nextToken());
        assertEquals(num, jp.getText());
        assertToken(JsonToken.END_ARRAY, jp.nextToken());
    }
    private void _testIssue160LongNumbers(JsonFactory f, String doc, boolean useStream) throws Exception
    {
        JsonParser jp = useStream
                ? FACTORY.createParser(doc.getBytes("UTF-8"))
                        : FACTORY.createParser(doc);
        assertToken(JsonToken.VALUE_NUMBER_INT, jp.nextToken());
        BigInteger v = jp.getBigIntegerValue();
        assertNull(jp.nextToken());
        assertEquals(doc, v.toString());
    }
    private void _testLongerFloat(JsonParser p, String text) throws IOException
    {
        assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
        assertEquals(text, p.getText());
        assertNull(p.nextToken());
    }
    /**********************************************************
     */

    private String toJsonArray(double v, int n) {
        StringBuilder sb = new StringBuilder().append('[').append(v);
        for (int i = 1; i < n; ++i) {
            sb.append(',').append(v);
        }
        return sb.append(']').toString();
    }
    public void testLongerFloatingPoint() throws Exception
    {
        StringBuilder input = new StringBuilder();
        for (int i = 1; i < 201; i++) {
            input.append(1);
        }
        input.append(".0");
        final String DOC = input.toString();

        // test out with both Reader and ByteArrayInputStream
        JsonParser p;

        p = FACTORY.createParser(new StringReader(DOC));
        _testLongerFloat(p, DOC);
        p.close();
        
        p = FACTORY.createParser(new ByteArrayInputStream(DOC.getBytes("UTF-8")));
        _testLongerFloat(p, DOC);
        p.close();
    }
}
