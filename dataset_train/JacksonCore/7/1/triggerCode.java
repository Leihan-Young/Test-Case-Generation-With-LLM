package com.fasterxml.jackson.core.json;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
public class GeneratorFailTest
    extends com.fasterxml.jackson.core.BaseTest
{
    private final JsonFactory F = new JsonFactory();
    /**********************************************************
     */

    private void _testDupFieldNameWrites(JsonFactory f, boolean useReader) throws Exception
    {
        JsonGenerator gen;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        if (useReader) {
            gen = f.createGenerator(new OutputStreamWriter(bout, "UTF-8"));
        } else {
            gen = f.createGenerator(bout, JsonEncoding.UTF8);
        }
        gen.writeStartObject();
        gen.writeFieldName("a");
        
        try {
            gen.writeFieldName("b");
            gen.flush();
            String json = bout.toString("UTF-8");
            fail("Should not have let two consecutive field name writes succeed: output = "+json);
        } catch (JsonProcessingException e) {
            verifyException(e, "can not write a field name, expecting a value");
        }
        gen.close();
    }
    private void _testFailOnWritingStringNotFieldName(JsonFactory f, boolean useReader) throws Exception
    {
        JsonGenerator gen;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        if (useReader) {
            gen = f.createGenerator(new OutputStreamWriter(bout, "UTF-8"));
        } else {
            gen = f.createGenerator(bout, JsonEncoding.UTF8);
        }
        gen.writeStartObject();
        
        try {
            gen.writeString("a");
            gen.flush();
            String json = bout.toString("UTF-8");
            fail("Should not have let "+gen.getClass().getName()+".writeString() be used in place of 'writeFieldName()': output = "+json);
        } catch (JsonProcessingException e) {
            verifyException(e, "can not write a String");
        }
        gen.close();
    }
    public void testFailOnWritingStringNotFieldNameBytes() throws Exception {
        _testFailOnWritingStringNotFieldName(F, false);
    }
}
